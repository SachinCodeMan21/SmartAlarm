package com.example.smartalarm.feature.alarm.framework.notification.mapper

import android.content.Context
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.framework.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.framework.notification.mapper.AlarmNotificationDataMapperHelper.createContentIntent
import com.example.smartalarm.feature.alarm.framework.notification.mapper.AlarmNotificationDataMapperHelper.createDismissIntent
import javax.inject.Inject

/**
 * Maps [AlarmNotificationModel.MissedAlarmModel] into [AlarmNotificationData] for missed alarm notifications.
 *
 * This class is responsible for building the notification data shown when a user misses an alarm,
 * including:
 * - A fixed "Missed Alarm" title.
 * - The originally scheduled alarm time.
 * - A "Dismiss" action allowing the user to acknowledge the missed alarm.
 * - An intent to open the app when the notification is tapped.
 *
 * Designed to work with the app's notification system to display consistent missed alarm messages.
 *
 * @constructor Injects an instance using dependency injection.
 */
class MissedAlarmNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<AlarmNotificationModel.MissedAlarmModel, AlarmNotificationData> {

    /**
     * Converts a [AlarmNotificationModel.MissedAlarmModel] into an [AlarmNotificationData] object suitable for notification display.
     *
     * @param context The context used to access resources and create [android.app.PendingIntent]s.
     * @param model The model containing the missed alarm data.
     * @return A [AlarmNotificationData] instance populated with title, time, actions, and content intent.
     */
    override fun map(context: Context, model: AlarmNotificationModel.MissedAlarmModel): AlarmNotificationData {
        val alarm = model.alarm

        return AlarmNotificationData(
            id = alarm.id,
            title = context.getString(R.string.missed_alarm),
            contentText = timeFormatter.getFormattedDayAndTime(model.alarm.time.hour, model.alarm.time.minute),
            actions = listOf(
                NotificationAction(
                    id = alarm.id,
                    title = context.getString(R.string.dismiss_alarm),
                    icon = R.drawable.ic_delete,
                    pendingIntent = createDismissIntent(context, alarm.id)
                )
            ),
            contentIntent = createContentIntent(context, alarm.id)
        )
    }
}

