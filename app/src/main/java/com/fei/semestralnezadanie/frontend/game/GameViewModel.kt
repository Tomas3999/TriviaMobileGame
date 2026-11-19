package com.fei.semestralnezadanie.frontend.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.R
import com.fei.semestralnezadanie.backend.database.entities.QuestionAnswer
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.repository.ApiException
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import com.fei.semestralnezadanie.backend.repository.models.TriviaQuestion
import com.fei.semestralnezadanie.enums.Difficulty
import com.fei.semestralnezadanie.enums.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Int? = savedStateHandle.get<Int>("categoryId")?.takeIf { it != -1 }
    private val categoryName: String = savedStateHandle["categoryName"] ?: "Any"
    private val difficultyStr: String? = savedStateHandle["difficulty"]
    private val typeStr: String? = savedStateHandle["type"]
    private val gameModeId: Int = savedStateHandle["gameModeId"] ?: GameMode.CLASSIC.id

    val isMarathon: Boolean = gameModeId == GameMode.MARATHON.id

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val questionsPool = mutableListOf<TriviaQuestion>()
    private val recordedAnswers = mutableListOf<QuestionAnswer>()

    private var timerJob: Job? = null

    init {
        loadInitialQuestions()
    }

    private fun loadInitialQuestions() {
        val amount = if (isMarathon) Config.Trivia.MARATHON_BATCH_SIZE else Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null) }

            triviaRepository.getQuestions(
                amount = amount,
                categoryId = categoryId,
                difficulty = difficultyStr,
                type = typeStr
            ).onSuccess { questions ->
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
                        totalQuestionsCount = if (isMarathon) 1 else Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT
                    )
                }
                startSingleTimer()
            }.onFailure { exception ->
                val errorRes = if (exception is ApiException) exception.responseCode.messageRes else R.string.api_error_unknown
                _uiState.update { it.copy(isLoading = false, errorResId = errorRes) }
            }
        }
    }

    private fun startSingleTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(remainingSeconds = Config.Trivia.QUESTION_TIME_LIMIT_SECONDS) }

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
        if (!isMarathon && nextIndex >= Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT) {
            finishGame()
        } else if (nextIndex < questionsPool.size) {
            _uiState.update {
                it.copy(
                    currentQuestion = questionsPool[nextIndex],
                    currentQuestionIndex = nextIndex + 1,
                    totalQuestionsCount = if (isMarathon) nextIndex + 1 else Config.Trivia.CLASSIC_TRIVIA_QUESTION_COUNT
                )
            }
            startSingleTimer()

            if (isMarathon && (questionsPool.size - nextIndex) <= 5) {
                fetchNextMarathonBatch()
            }
        } else {
            finishGame()
        }
    }

    private fun fetchNextMarathonBatch() {
        if (_uiState.value.isFetchingMoreQuestions) return

        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingMoreQuestions = true) }

            triviaRepository.getQuestions(
                amount = Config.Trivia.MARATHON_BATCH_SIZE,
                categoryId = categoryId,
                difficulty = difficultyStr,
                type = typeStr
            ).onSuccess { newBatch ->
                questionsPool.addAll(newBatch)
                _uiState.update { it.copy(isFetchingMoreQuestions = false) }
            }.onFailure {
                _uiState.update { it.copy(isFetchingMoreQuestions = false) }
            }
        }
    }

    private fun finishGame() {
        timerJob?.cancel()

        viewModelScope.launch {
            var isPersonalBest = false

            if (isMarathon) {
                val previousBest = triviaRepository.getMarathonPersonalBest() ?: 0
                if (_uiState.value.correctAnswersCount > previousBest) {
                    isPersonalBest = true
                }
            }

            val triviaResult = TriviaResult(
                timestamp = System.currentTimeMillis(),
                category = categoryName,
                difficulty = Difficulty.fromApiValue(difficultyStr).id, // <- OPRAVENÉ (.id namiesto .ordinal)
                gameMode = gameModeId,
                durationSeconds = _uiState.value.totalGameDurationSeconds,
                totalQuestionsCount = recordedAnswers.size,
                correctAnswersCount = _uiState.value.correctAnswersCount,
                incorrectAnswersCount = _uiState.value.incorrectAnswersCount
            )

            val quizId = triviaRepository.saveQuizResult(triviaResult, recordedAnswers)

            _uiState.update {
                it.copy(
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