package com.example.smartalarm.feature.timer.framework.notification.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveTimerNotificationBuilder @Inject constructor() :
    AppNotificationBuilder<TimerNotification.ActiveTimer> {
    override fun buildNotification(context: Context, notificationType: TimerNotification.ActiveTimer): Notification {
        val data = notificationType.data
        val builder = NotificationCompat.Builder(context, AppNotificationChannel.ACTIVE_TIMER.channelId)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle(context.getString(R.string.active_timer))
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.formattedBigText))
            .setContentIntent(data.contentIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        data.actions.forEach { action ->
            builder.addAction(action.icon?:0,action.title,action.pendingIntent)
        }

        return builder.build()
    }
}