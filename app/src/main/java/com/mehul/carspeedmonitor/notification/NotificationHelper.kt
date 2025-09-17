package com.mehul.carspeedmonitor.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper (private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "speed_alert_channel"
        private const val CHANNEL_NAME = "Speed Alerts"
        private const val NOTIFICATION_ID = 1001
    }
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // Heads-up
            ).apply {
                description = "Notifications when vehicle speed exceeds the limit"
                enableVibration(true)
            }

            val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    fun showSpeedAlert(currentSpeed: Float, maxSpeed: Float) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

            .setContentTitle("Speed Limit Exceeded")
            .setContentText("Your speed $currentSpeed km/h > Limit $maxSpeed km/h")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ensures HUN
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
    fun clearNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}