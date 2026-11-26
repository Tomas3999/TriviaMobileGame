package com.fei.semestralnezadanie.backend.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fei.semestralnezadanie.Config
import com.fei.semestralnezadanie.MainActivity
import com.fei.semestralnezadanie.R

object NotificationHelper {
     /**
     * Vytvorí notifikačný kanál pre Android 8.0+ (bezpečné volať opakovane).
     */
    fun createNotificationChannel(context: Context) {
         val channel = NotificationChannel(
             Config.Notifications.CHANNEL_ID,
             Config.Notifications.CHANNEL_NAME,
             NotificationManager.IMPORTANCE_DEFAULT
         )
         val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         manager.createNotificationChannel(channel)
     }

    fun showStreakReminder(context: Context) {
        showNotification(
            context = context,
            id = Config.Notifications.NOTIFICATION_ID_STREAK,
            title = context.getString(R.string.notif_streak_title),
            message = context.getString(R.string.notif_streak_body)
        )
    }

    fun showInactivityReminder(context: Context) {
        showNotification(
            context = context,
            id = Config.Notifications.NOTIFICATION_ID_INACTIVITY,
            title = context.getString(R.string.notif_inactivity_title),
            message = context.getString(R.string.notif_inactivity_body)
        )
    }

    fun showMarathonRecordNotification(context: Context, newRecordScore: Int) {
        showNotification(
            context = context,
            id = Config.Notifications.NOTIFICATION_ID_MARATHON,
            title = context.getString(R.string.notif_marathon_title),
            message = context.getString(R.string.notif_marathon_body, newRecordScore)
        )
    }

    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        // Bezpečná kontrola povolenia pre Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) return
        }

        createNotificationChannel(context)

        // Po kliknutí na notifikáciu sa spustí MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, Config.Notifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}