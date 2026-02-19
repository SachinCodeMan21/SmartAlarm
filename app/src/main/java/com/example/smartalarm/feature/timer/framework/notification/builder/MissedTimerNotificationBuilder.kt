package com.example.smartalarm.feature.timer.framework.notification.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotification
import javax.inject.Inject

class MissedTimerNotificationBuilder @Inject constructor() :
    AppNotificationBuilder<TimerNotification.MissedTimer> {
    override fun buildNotification(context: Context, notificationType: TimerNotification.MissedTimer): Notification {
        val data = notificationType.data
        val builder = NotificationCompat.Builder(context, AppNotificationChannel.MISSED_ALARM.channelId)
            .setSmallIcon(R.drawable.ic_alarm) // Use a missed alarm icon
            .setContentTitle(context.getString(R.string.missed_timer)) // The title
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.formattedBigText)) // The content
            .setContentIntent(data.contentIntent) // Optional: content intent
            .setOnlyAlertOnce(true)
            .setAutoCancel(true) // Notification will be removed after the user taps it

        return builder.build()
    }
}