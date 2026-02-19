package com.example.smartalarm.feature.alarm.framework.notification.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotification
import com.example.smartalarm.feature.alarm.framework.notification.model.NotificationGroupKeys
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import javax.inject.Inject

/**
 * Singleton builder class responsible for creating notifications for snoozed alarms.
 *
 * Implements [AppNotificationBuilder] for [AlarmNotification.Snoozed] type,
 * constructing the notification UI using data from [AlarmNotificationData].
 *
 * The notification includes:
 * - A small alarm icon.
 * - A title indicating the alarm is snoozed.
 * - The snoozed alarm time as content text.
 * - Tap action via [android.app.PendingIntent].
 * - Auto-cancel behavior on tap.
 * - Grouping under the snoozed alarms notification group.
 * - Action buttons as specified in the notification data.
 *
 * @constructor Injected via Dagger for dependency management.
 */
class SnoozedAlarmNotificationBuilder @Inject constructor() : AppNotificationBuilder<AlarmNotification.Snoozed> {

    /**
     * Builds the notification UI for a snoozed alarm.
     *
     * @param context The context used to access resources and create the notification.
     * @param notificationType The [AlarmNotification.Snoozed] containing the notification data.
     * @return A fully built [Notification] instance ready to be displayed.
     */
    override fun buildNotification(context: Context, notificationType: AlarmNotification.Snoozed): Notification {
        val notificationData = notificationType.alarmData

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.SNOOZED_ALARM.channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(context.getString(R.string.snoozed_alarm))
            .setContentText(notificationData.contentText)
            .setContentIntent(notificationData.contentIntent)
            .setAutoCancel(true)
            .setGroup(NotificationGroupKeys.SNOOZED_ALARMS)

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
