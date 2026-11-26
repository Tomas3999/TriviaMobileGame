package com.fei.semestralnezadanie.backend.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.backend.database.dao.TriviaDao
import com.fei.semestralnezadanie.backend.datastore.UserManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

@EntryPoint
@InstallIn(SingletonComponent::class)
interface QuizReminderWorkerEntryPoint {
    fun getUserManager(): UserManager
    fun getTriviaDao(): TriviaDao
}

class QuizReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                QuizReminderWorkerEntryPoint::class.java
            )
            val userManager = entryPoint.getUserManager()
            val triviaDao = entryPoint.getTriviaDao()

            val profile = userManager.userProfileFlow.first()

            // 1. Ak používateľ vypol notifikácie alebo nedokončil onboarding, končíme
            if (!profile.isOnboardingCompleted || !profile.notificationsEnabled) {
                return Result.success()
            }

            // 2. Načítanie histórie kvízov z Hilt-injected Room databázy
            val results = triviaDao.getAllResults().first()

            val now = System.currentTimeMillis()
            val lastPlayedTimestamp = results.maxOfOrNull { it.timestamp } ?: 0L

            // 3. Kontrola dlhšej nečinnosti (napr. >= 3 dni)
            if (results.isNotEmpty()) {
                val daysSinceLastPlay = TimeUnit.MILLISECONDS.toDays(now - lastPlayedTimestamp)
                if (daysSinceLastPlay >= Config.Notifications.INACTIVITY_THRESHOLD_DAYS) {
                    NotificationHelper.showInactivityReminder(applicationContext)
                    return Result.success()
                }
            }

            // 4. Kontrola dnešného testu pre 7-dňový Streak (úspešnosť >= 80 %)
            val playedQualifyingToday = results.any { result ->
                isToday(result.timestamp) &&
                        result.totalQuestionsCount > 0 &&
                        ((result.correctAnswersCount.toDouble() / result.totalQuestionsCount.toDouble()) * 100.0) >=
                        Config.Gamification.STREAK_MIN_ACCURACY_PERCENT
            }

            if (!playedQualifyingToday) {
                NotificationHelper.showStreakReminder(applicationContext)
            }

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun isToday(timestampMillis: Long): Boolean {
        val target = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val today = Calendar.getInstance()
        return target.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                target.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
}