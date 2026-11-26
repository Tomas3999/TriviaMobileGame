package com.fei.semestralnezadanie.frontend.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import com.fei.semestralnezadanie.enums.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class CategoryMasteryItem(
    val categoryName: String,
    val correctCount: Int,
    val totalInApi: Int?
) {
    val progress: Float
        get() = if (totalInApi != null && totalInApi > 0) {
            (correctCount.toFloat() / totalInApi.toFloat()).coerceIn(0f, 1f)
        } else 0f
}

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val isTodayCompleted: Boolean = false,
    val marathonPersonalBest: Int = 0,
    val streakTargetDays: Int = Config.Gamification.STREAK_TARGET_DAYS,
    val categoryMasteryList: List<CategoryMasteryItem> = emptyList()
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        observeChallengesData()
        observeCategorySync()
    }

    private fun observeChallengesData() {
        viewModelScope.launch {
            combine(
                triviaRepository.getAllResults(),
                triviaRepository.getCategoryCorrectCounts(),
                triviaRepository.getAllCategoryInfos()
            ) { results, correctCounts, categoryInfos ->

                // 1. Streak výpočet
                val streakInfo = calculateStreak(results)

                // 2. Reaktívny rekord v Maratóne (aktualizuje sa okamžite po každej hre!)
                val marathonPb = results
                    .filter { it.gameMode == GameMode.MARATHON.id }
                    .maxOfOrNull { it.correctAnswersCount } ?: 0

                // 3. Pokrok v kategóriách (proti všetkým otázkam z OpenTDB)
                val totalsMap = categoryInfos.associateBy({ it.categoryName }, { it.totalQuestionsCount })

                val masteryList = correctCounts.map { stat ->
                    CategoryMasteryItem(
                        categoryName = stat.category,
                        correctCount = stat.correctCount,
                        totalInApi = totalsMap[stat.category]
                    )
                }

                ChallengesUiState(
                    isLoading = false,
                    currentStreak = streakInfo.streakCount,
                    isTodayCompleted = streakInfo.isTodayCompleted,
                    marathonPersonalBest = marathonPb,
                    categoryMasteryList = masteryList
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    // Samostatné sledovanie kategórií pre stiahnutie chýbajúcich/starých počtov z api_count.php
    private fun observeCategorySync() {
        viewModelScope.launch {
            triviaRepository.getCategoryCorrectCounts()
                .map { list -> list.map { it.category } }
                .distinctUntilChanged()
                .collect { playedCategoryNames ->
                    if (playedCategoryNames.isNotEmpty()) {
                        triviaRepository.syncMissingOrExpiredCategoryCounts(playedCategoryNames)
                    }
                }
        }
    }

    private fun calculateStreak(results: List<TriviaResult>): StreakResult {
        if (results.isEmpty()) return StreakResult(0, false)

        val qualifyingResults = results.filter { result ->
            if (result.totalQuestionsCount == 0) false
            else {
                val accuracy = (result.correctAnswersCount.toDouble() / result.totalQuestionsCount.toDouble()) * 100.0
                accuracy >= Config.Gamification.STREAK_MIN_ACCURACY_PERCENT
            }
        }

        if (qualifyingResults.isEmpty()) return StreakResult(0, false)

        val epochDays = qualifyingResults.map { getEpochDay(it.timestamp) }
            .distinct()
            .sortedDescending()

        val todayEpoch = getEpochDay(System.currentTimeMillis())
        val isTodayCompleted = epochDays.contains(todayEpoch)

        var streak = 0
        var expectedDay = if (isTodayCompleted) todayEpoch else todayEpoch - 1

        for (day in epochDays) {
            if (day == expectedDay) {
                streak++
                expectedDay--
                if (streak == Config.Gamification.STREAK_TARGET_DAYS) break
            } else if (day < expectedDay) {
                break
            }
        }

        return StreakResult(
            streakCount = streak.coerceAtMost(Config.Gamification.STREAK_TARGET_DAYS),
            isTodayCompleted = isTodayCompleted
        )
    }

    private fun getEpochDay(timestampMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestampMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return TimeUnit.MILLISECONDS.toDays(cal.timeInMillis)
    }

    private data class StreakResult(
        val streakCount: Int,
        val isTodayCompleted: Boolean
    )
}