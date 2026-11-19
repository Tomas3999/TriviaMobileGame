package com.fei.semestralnezadanie.frontend.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.database.entities.TriviaResult
import com.fei.semestralnezadanie.backend.repository.TriviaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val isTodayCompleted: Boolean = false,
    val marathonPersonalBest: Int = 0,
    val streakTargetDays: Int = Config.Gamification.STREAK_TARGET_DAYS
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        loadChallengesData()
    }

    private fun loadChallengesData() {
        viewModelScope.launch {
            // 1. Získanie rekordu v maratóne
            val marathonPb = triviaRepository.getMarathonPersonalBest() ?: 0

            // 2. Sledovanie histórie pre výpočet Streaku
            triviaRepository.getAllResults().collect { results ->
                val streakInfo = calculateStreak(results)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStreak = streakInfo.streakCount,
                        isTodayCompleted = streakInfo.isTodayCompleted,
                        marathonPersonalBest = marathonPb
                    )
                }
            }
        }
    }

    /**
     * Algoritmus pre 7 in a row (Streak):
     * - Hra sa počíta, len ak úspešnosť >= 80 %
     * - Kontroluje po sebe idúce dni
     * - Reset pri vynechaní dňa
     */
    private fun calculateStreak(results: List<TriviaResult>): StreakResult {
        if (results.isEmpty()) return StreakResult(0, false)

        // 1. Filtrujeme hry s úspešnosťou >= 80 %
        val qualifyingResults = results.filter { result ->
            if (result.totalQuestionsCount == 0) false
            else {
                val accuracy = (result.correctAnswersCount.toDouble() / result.totalQuestionsCount.toDouble()) * 100.0
                accuracy >= Config.Gamification.STREAK_MIN_ACCURACY_PERCENT
            }
        }

        if (qualifyingResults.isEmpty()) return StreakResult(0, false)

        // 2. Zoskupenie do kalendárnych dní (epoch days)
        val epochDays = qualifyingResults.map { getEpochDay(it.timestamp) }
            .distinct()
            .sortedDescending()

        val todayEpoch = getEpochDay(System.currentTimeMillis())
        val isTodayCompleted = epochDays.contains(todayEpoch)

        var streak = 0
        var expectedDay = if (isTodayCompleted) todayEpoch else todayEpoch - 1

        // 3. Počítanie po sebe idúcich dní dozadu
        for (day in epochDays) {
            if (day == expectedDay) {
                streak++
                expectedDay--
                // Maximálny cieľ je 7 dní
                if (streak == Config.Gamification.STREAK_TARGET_DAYS) break
            } else if (day < expectedDay) {
                // Vynechal deň -> koniec reťazca
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