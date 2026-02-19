package com.example.smartalarm.feature.alarm.framework.notification.builder

import android.app.Notification
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotification
import javax.inject.Inject

/**
 * Builds a high-priority notification for scheduled alarms using the provided [AlarmNotification.Scheduled] data.
 *
 * This builder is responsible for constructing the notification UI when an alarm is currently ringing
 * or about to ring. It includes:
 * - A full-screen intent to immediately display the alarm screen.
 * - Action buttons such as "Snooze" or "Stop/Complete".
 * - A custom color, category, and high priority to signal urgency.
 *
 * Injected via Hilt to allow Dagger-based instantiation and usage.
 *
 * @constructor Injects the builder through Hilt.
 */
class ScheduledAlarmNotificationBuilder @Inject constructor() : AppNotificationBuilder<AlarmNotification.Scheduled> {

    /**
     * Builds and returns a [Notification] object for a scheduled alarm.
     *
     * The notification uses the `SCHEDULED_ALARM` channel and includes:
     * - Title and content based on the alarm.
     * - Optional action buttons.
     * - A full-screen intent to show the alarm UI.
     *
     * @param context The application context used to fetch resources and create intents.
     * @param notificationType The notification wrapper containing alarm data to build the UI.
     * @return A fully configured [Notification] ready to be shown.
     */
    override fun buildNotification(context: Context, notificationType: AlarmNotification.Scheduled): Notification {
        val notificationData = notificationType.alarmData

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.SCHEDULED_ALARM.channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.alarm))
            .setContentText(notificationData.contentText)
            .setContentIntent(notificationData.contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setFullScreenIntent(notificationData.fullScreenIntent, true)
            .setColor(context.resources.getColor(R.color.darkRed, null))
            .setColorized(true)
            .setOngoing(true)
            .setAutoCancel(false)

        // Add each action from the notification data as an action button.
        notificationData.actions.forEach { action ->
            builder.addAction(
                NotificationCompat.Action(
                    action.icon ?: R.drawable.ic_alarm,
                    action.title,
                    action.pendingIntent
                )
            )
        }

        return builder.build()
    }

}
