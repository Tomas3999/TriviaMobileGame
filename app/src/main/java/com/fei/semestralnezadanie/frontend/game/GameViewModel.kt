package com.fei.semestralnezadanie.frontend.game

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.datastore.UserManager
import com.fei.semestralnezadanie.backend.notifications.NotificationHelper
import com.fei.semestralnezadanie.backend.repository.ApiException
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import com.fei.semestralnezadanie.backend.repository.models.TriviaQuestion
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode
import com.fei.semestralnezadanie.enums.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val isLoading: Boolean = true,
    val isFetchingMoreQuestions: Boolean = false,
    val errorResId: Int? = null,
    val currentQuestion: TriviaQuestion? = null,
    val currentQuestionIndex: Int = 1,
    val totalQuestionsCount: Int = Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT,
    val remainingSeconds: Long = Config.Trivia.QUESTION_TIME_LIMIT_SECONDS,
    val totalGameDurationSeconds: Long = 0L,
    val correctAnswersCount: Int = 0,
    val incorrectAnswersCount: Int = 0,
    val isGameOver: Boolean = false,
    val isNewPersonalBestInMarathon: Boolean = false,
    val savedQuizId: Long = -1L
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository,
    private val userManager: UserManager,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Int? = savedStateHandle.get<Int>("categoryId")?.takeIf { it != -1 }
    private val categoryName: String = savedStateHandle["categoryName"] ?: "Any"
    private val difficultyStr: String? = savedStateHandle.get<String>("difficulty")?.takeIf { it.isNotBlank() }
    private val typeStr: String? = savedStateHandle.get<String>("type")?.takeIf { it.isNotBlank() }
    private val gameModeId: Int = savedStateHandle["gameModeId"] ?: GameMode.CLASSIC.id

    val isMarathon: Boolean = gameModeId == GameMode.MARATHON.id

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val questionsPool = mutableListOf<TriviaQuestion>()
    private val recordedAnswers = mutableListOf<QuestionAnswer>()

    // Preventívny počet otázok + ochrana proti Rate Limitu
    private var remainingEstimateInApi: Int? = null
    private var currentBatchIndex = 0
    private var lastApiCallTimestamp = 0L

    private var sessionToken: String? = null
    private var hasMoreQuestionsInApi = true
    private var isWaitingForNextBatch = false

    private var timerJob: Job? = null

    init {
        loadInitialQuestions()
    }

    /**
     * Stiahne otázky s hybridnou logikou:
     * 1. Ak kategória má <= 50 otázok, stiahne ich bez filtra typu a vyfiltruje ich lokálne v mobile (okamžité, 0 chýb).
     * 2. Ak má > 50 otázok (alebo Any), pošle filter typu na server a v zálohe má zmenšovanie dávky.
     */
    private suspend fun fetchQuestionsSmart(): Result<List<TriviaQuestion>> {
        val estimate = remainingEstimateInApi

        // Hybridné rozhodnutie: Pri malej kategórii (<= 50 otázok) vyfiltrujeme typ lokálne v mobile
        val shouldFilterTypeLocally = (estimate != null && estimate <= Config.Trivia.MARATHON_BATCH_SIZE && typeStr != null)

        // Pre Klasický mód (ak nie je malá kategória na lokálne filtrovanie) stiahneme priamo 10
        if (!isMarathon && !shouldFilterTypeLocally) {
            lastApiCallTimestamp = System.currentTimeMillis()
            return triviaRepository.getQuestions(
                amount = Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT,
                categoryId = categoryId,
                difficulty = difficultyStr,
                type = typeStr
            )
        }

        // Koľko otázok max pýtať na jeden dotaz
        val smartMaxAmount = if (estimate != null) {
            minOf(Config.Trivia.MARATHON_BATCH_SIZE, estimate)
        } else {
            Config.Trivia.MARATHON_BATCH_SIZE
        }

        if (smartMaxAmount <= 0) {
            hasMoreQuestionsInApi = false
            return Result.success(emptyList())
        }

        val apiTypeParam = if (shouldFilterTypeLocally) null else typeStr
        val dynamicBatches = (listOf(smartMaxAmount) + listOf(20, 10, 5, 1).filter { it < smartMaxAmount }).distinct()

        while (currentBatchIndex < dynamicBatches.size) {
            val amount = dynamicBatches[currentBatchIndex]

            // Rešpektovanie 5-sekundového Rate Limitu
            val elapsed = System.currentTimeMillis() - lastApiCallTimestamp
            if (lastApiCallTimestamp > 0L && elapsed < 5200L) {
                delay(5200L - elapsed)
            }

            lastApiCallTimestamp = System.currentTimeMillis()
            val result = triviaRepository.getQuestions(
                amount = amount,
                categoryId = categoryId,
                difficulty = difficultyStr,
                type = apiTypeParam,
                token = if (isMarathon) sessionToken else null
            )

            if (result.isSuccess) {
                val rawList = result.getOrThrow()

                // Odpočítame surový stiahnutý počet zo zostatku v API
                remainingEstimateInApi = remainingEstimateInApi?.let { (it - rawList.size).coerceAtLeast(0) }
                if (remainingEstimateInApi == 0) {
                    hasMoreQuestionsInApi = false
                }

                // Ak sme stiahli bez filtra typu, vyfiltrujeme ho lokálne teraz
                val finalList = if (shouldFilterTypeLocally && typeStr != null) {
                    rawList.filter { it.type == typeStr }
                } else {
                    rawList
                }

                // Pre Klasický mód vezmeme max 10 otázok
                val trimmedList = if (!isMarathon) {
                    finalList.take(Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT)
                } else {
                    finalList
                }

                return Result.success(trimmedList)
            } else {
                val ex = result.exceptionOrNull()
                if (ex is ApiException) {
                    when (ex.responseCode) {
                        ResponseCode.NO_RESULTS, ResponseCode.TOKEN_EMPTY -> currentBatchIndex++
                        ResponseCode.RATE_LIMIT -> delay(5200L)
                        ResponseCode.TOKEN_NOT_FOUND -> sessionToken = triviaRepository.requestSessionToken().getOrNull()
                        else -> return result
                    }
                } else {
                    return result
                }
            }
        }

        hasMoreQuestionsInApi = false
        return Result.success(emptyList())
    }

    fun loadInitialQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null) }

            // 1. Preventívne zistíme počet otázok pre kategóriu (ak je konkrétna kategória zvolená)
            if (categoryId != null) {
                remainingEstimateInApi = triviaRepository.getCategoryQuestionCount(categoryId, difficultyStr).getOrNull()
                if (remainingEstimateInApi == 0) {
                    _uiState.update { it.copy(isLoading = false, errorResId = R.string.api_error_no_results) }
                    return@launch
                }
            }

            // 2. Pre Maratón získame Session Token
            if (isMarathon) {
                sessionToken = triviaRepository.requestSessionToken().getOrNull()
            }
            currentBatchIndex = 0

            // 3. Stiahnutie prvej dávky otázok
            fetchQuestionsSmart()
                .onSuccess { questions ->
                    if (questions.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false, errorResId = R.string.api_error_no_results) }
                        return@onSuccess
                    }

                    questionsPool.clear()
                    questionsPool.addAll(questions)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentQuestion = questionsPool.firstOrNull(),
                            currentQuestionIndex = 1,
                            totalQuestionsCount = if (isMarathon) 1 else questionsPool.size
                        )
                    }
                    startSingleTimer(resetRemaining = true)
                }
                .onFailure { exception ->
                    val errorRes = if (exception is ApiException) exception.responseCode.messageRes else R.string.api_error_unknown
                    _uiState.update { it.copy(isLoading = false, errorResId = errorRes) }
                }
        }
    }

    private fun startSingleTimer(resetRemaining: Boolean = true) {
        timerJob?.cancel()
        if (resetRemaining) {
            _uiState.update { it.copy(remainingSeconds = Config.Trivia.QUESTION_TIME_LIMIT_SECONDS) }
        }

        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000L)
                _uiState.update {
                    it.copy(
                        remainingSeconds = it.remainingSeconds - 1,
                        totalGameDurationSeconds = it.totalGameDurationSeconds + 1
                    )
                }
            }
            submitAnswer(null)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun resumeTimer() {
        val state = _uiState.value
        if (!state.isLoading && !state.isGameOver && state.currentQuestion != null && timerJob?.isActive != true) {
            startSingleTimer(resetRemaining = false)
        }
    }

    fun endMarathonEarly() {
        if (!isMarathon || _uiState.value.isGameOver) return
        finishGame()
    }

    fun submitAnswer(selectedAnswerIndex: Int?) {
        timerJob?.cancel()

        val currentQuestion = _uiState.value.currentQuestion ?: return
        val isCorrect = selectedAnswerIndex != null && selectedAnswerIndex == currentQuestion.correctAnswerIndex
        val timeSpent = Config.Trivia.QUESTION_TIME_LIMIT_SECONDS - _uiState.value.remainingSeconds

        recordedAnswers.add(
            QuestionAnswer(
                triviaResultId = 0,
                questionText = currentQuestion.question,
                options = currentQuestion.options,
                selectedAnswer = selectedAnswerIndex,
                correctAnswer = currentQuestion.correctAnswerIndex,
                category = currentQuestion.category,
                timeSpentSeconds = timeSpent
            )
        )

        val newCorrect = if (isCorrect) _uiState.value.correctAnswersCount + 1 else _uiState.value.correctAnswersCount
        val newIncorrect = if (!isCorrect) _uiState.value.incorrectAnswersCount + 1 else _uiState.value.incorrectAnswersCount

        _uiState.update {
            it.copy(
                correctAnswersCount = newCorrect,
                incorrectAnswersCount = newIncorrect
            )
        }

        if (isMarathon && !isCorrect) {
            finishGame()
            return
        }

        val nextIndex = _uiState.value.currentQuestionIndex

        if (!isMarathon && nextIndex >= _uiState.value.totalQuestionsCount) {
            finishGame()
        } else if (nextIndex < questionsPool.size) {
            advanceToQuestion(nextIndex)

            val threshold = minOf(10, questionsPool.size / 2).coerceAtLeast(2)
            if (isMarathon && hasMoreQuestionsInApi && (questionsPool.size - nextIndex) <= threshold) {
                fetchNextMarathonBatch()
            }
        } else if (isMarathon && _uiState.value.isFetchingMoreQuestions) {
            isWaitingForNextBatch = true
            _uiState.update { it.copy(isLoading = true) }
        } else {
            finishGame()
        }
    }

    private fun advanceToQuestion(index: Int) {
        _uiState.update {
            it.copy(
                isLoading = false,
                currentQuestion = questionsPool[index],
                currentQuestionIndex = index + 1,
                totalQuestionsCount = if (isMarathon) index + 1 else _uiState.value.totalQuestionsCount
            )
        }
        startSingleTimer(resetRemaining = true)
    }

    private fun fetchNextMarathonBatch() {
        if (_uiState.value.isFetchingMoreQuestions || !hasMoreQuestionsInApi) return

        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingMoreQuestions = true) }
            currentBatchIndex = 0

            fetchQuestionsSmart()
                .onSuccess { newBatch ->
                    if (newBatch.isEmpty()) {
                        hasMoreQuestionsInApi = false
                        _uiState.update { it.copy(isFetchingMoreQuestions = false) }
                        if (isWaitingForNextBatch) {
                            isWaitingForNextBatch = false
                            finishGame()
                        }
                    } else {
                        questionsPool.addAll(newBatch)
                        _uiState.update { it.copy(isFetchingMoreQuestions = false) }

                        if (isWaitingForNextBatch) {
                            isWaitingForNextBatch = false
                            val nextIndex = _uiState.value.currentQuestionIndex
                            if (nextIndex < questionsPool.size) {
                                advanceToQuestion(nextIndex)
                            } else {
                                finishGame()
                            }
                        }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isFetchingMoreQuestions = false) }
                    if (isWaitingForNextBatch) {
                        isWaitingForNextBatch = false
                        finishGame()
                    }
                }
        }
    }

    private fun finishGame() {
        timerJob?.cancel()

        viewModelScope.launch {
            var isPersonalBest = false
            var quizId = -1L

            try {
                if (isMarathon) {
                    val previousBest = triviaRepository.getMarathonPersonalBest() ?: 0
                    if (_uiState.value.correctAnswersCount > previousBest) {
                        isPersonalBest = true

                        val notificationsEnabled = userManager.userProfileFlow.first().notificationsEnabled
                        if (notificationsEnabled) {
                            NotificationHelper.showMarathonRecordNotification(
                                context = context,
                                newRecordScore = _uiState.value.correctAnswersCount
                            )
                        }
                    }
                }

                if (recordedAnswers.isNotEmpty()) {
                    val triviaResult = TriviaResult(
                        timestamp = System.currentTimeMillis(),
                        category = categoryName,
                        difficulty = Difficulty.fromApiValue(difficultyStr).id,
                        gameMode = gameModeId,
                        durationSeconds = _uiState.value.totalGameDurationSeconds,
                        totalQuestionsCount = recordedAnswers.size,
                        correctAnswersCount = _uiState.value.correctAnswersCount,
                        incorrectAnswersCount = _uiState.value.incorrectAnswersCount
                    )
                    quizId = triviaRepository.saveQuizResult(triviaResult, recordedAnswers)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isGameOver = true,
                    isNewPersonalBestInMarathon = isPersonalBest,
                    savedQuizId = quizId
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}