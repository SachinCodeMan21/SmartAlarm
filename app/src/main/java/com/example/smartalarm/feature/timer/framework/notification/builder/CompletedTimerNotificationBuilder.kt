package com.example.smartalarm.feature.timer.framework.notification.builder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotification
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletedTimerNotificationBuilder @Inject constructor() : AppNotificationBuilder<TimerNotification.CompletedTimer> {

    override fun buildNotification(context: Context, notificationType: TimerNotification.CompletedTimer): Notification {

        val data = notificationType.data
        val fullScreenPendingIntent = data.contentIntent

        Log.d("NOTIFICATION_DEBUG", "Building completed timer notification")
        Log.d("NOTIFICATION_DEBUG", "Full screen intent: ${notificationType.data.contentIntent}")


        val builder = NotificationCompat.Builder(context, AppNotificationChannel.COMPLETED_TIMER.channelId)
            .setContentTitle(context.getString(R.string.completed_timer))
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.formattedBigText))
            .setContentIntent(data.contentIntent)
            .setColor(context.resources.getColor(R.color.darkRed,null))
            .setColorized(true)
            .setSmallIcon(R.drawable.ic_timer)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)

        data.actions.forEach { action ->
            builder.addAction(action.icon?:0,action.title,action.pendingIntent)
        }

        return builder.build()
    }
}