package com.fei.semestralnezadanie

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fei.semestralnezadanie.backend.notifications.NotificationHelper
import com.fei.semestralnezadanie.backend.notifications.QuizReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Vytvorenie notifikačného kanála
        NotificationHelper.createNotificationChannel(this)

        // 2. Naplánovanie opakovanej kontroly cez WorkManager
        val reminderWorkRequest = PeriodicWorkRequestBuilder<QuizReminderWorker>(
            Config.Notifications.REMINDER_INTERVAL_HOURS,
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            Config.Notifications.STREAK_REMINDER_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWorkRequest
        )
    }
}