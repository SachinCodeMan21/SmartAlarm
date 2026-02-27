package com.example.smartalarm.feature.alarm.framework.notification.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.framework.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotification
import com.example.smartalarm.feature.alarm.framework.notification.model.NotificationGroupKeys
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.feature.alarm.framework.notification.factory.AlarmNotificationBuilderFactory
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import javax.inject.Inject


/**
 * Builder class responsible for creating notifications for **upcoming alarms**.
 *
 * This class implements [AppNotificationBuilder] for the [AlarmNotification.Upcoming] type.
 * It constructs a user-facing [Notification] that informs the user about an upcoming alarm,
 * using the display data provided by [AlarmNotificationData].
 *
 * ### Notification characteristics:
 * - Uses a small alarm icon (`ic_alarm`).
 * - Displays a title such as *"Upcoming Alarm"*.
 * - Shows the formatted alarm time as the content text.
 * - Provides a tap action through a [android.app.PendingIntent] to open the alarm screen.
 * - Automatically cancels itself when tapped.
 * - Groups under the *"Upcoming Alarms"* notification group ([NotificationGroupKeys.UPCOMING_ALARMS]).
 * - Includes custom action button (e.g., "Dismiss") as provided in [AlarmNotificationData.actions].
 *
 * ### Architecture:
 * - Managed and instantiated by [AlarmNotificationBuilderFactory].
 * - Invoked indirectly through [AlarmNotificationManager] when posting upcoming alarm notifications.
 * - Designed for dependency injection using Dagger/Hilt.
 *
 * @constructor Creates an instance via dependency injection.
 */

class UpcomingAlarmNotificationBuilder @Inject constructor() : AppNotificationBuilder<AlarmNotification.Upcoming> {

    /**
     * Builds a system [Notification] representing an upcoming alarm.
     *
     * @param context The Android [Context] used to access resources and system services.
     * @param notificationType The [AlarmNotification.Upcoming] containing the display data for the notification.
     * @return A fully configured [Notification] ready to be posted.
     */
    override fun buildNotification(context: Context, notificationType: AlarmNotification.Upcoming): Notification {

        val upcomingNotificationData = notificationType.alarmData

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.UPCOMING_ALARM.channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(context.getString(R.string.upcoming_alarm))
            .setContentText(upcomingNotificationData.contentText)
            .setContentIntent(upcomingNotificationData.contentIntent)
            .setAutoCancel(true)
            .setGroup(NotificationGroupKeys.UPCOMING_ALARMS)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

        // Add all configured actions (e.g. "Dismiss").
        upcomingNotificationData.actions.forEach { action ->
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


