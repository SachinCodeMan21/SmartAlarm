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
import java.util.Calendar
import javax.inject.Inject

/**
 * Maps [AlarmNotificationModel.SnoozedAlarmModel] into [AlarmNotificationData] for snoozed alarm notifications.
 *
 * This class is responsible for preparing the data shown when an alarm has been snoozed,
 * including:
 * - The label or a default title if no label is provided.
 * - The new alarm time after applying the snooze interval.
 * - A "Dismiss" action to cancel the snoozed alarm.
 * - A content intent to launch the app when the notification is tapped.
 *
 * This mapper is used by the app's notification system to display relevant information
 * for snoozed alarms in a consistent and user-friendly format.
 *
 * @constructor Injects an instance of [SnoozedAlarmNotificationDataMapper] using dependency injection.
 */
class SnoozedAlarmNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<AlarmNotificationModel.SnoozedAlarmModel, AlarmNotificationData> {

    /**
     * Maps the provided [com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel.SnoozedAlarmModel]
     * into an [AlarmNotificationData] object suitable for display.
     *
     * Computes the snoozed alarm time by adding the configured snooze interval to the original alarm time.
     *
     * @param context The context used for resource access and building [android.app.PendingIntent]s.
     * @param model The snoozed alarm model containing the alarm and snooze configuration.
     * @return A [AlarmNotificationData] object representing the snoozed alarm notification.
     */
    override fun map(context: Context, model: AlarmNotificationModel.SnoozedAlarmModel): AlarmNotificationData {
        val alarm = model.alarm

        // Get the current hour and minute from snoozeTimeInMillis
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = model.snoozeTimeInMillis

        // Get the hour and minute
//        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
//        val minute = calendar.get(Calendar.MINUTE)
        val formattedSnoozedTime = timeFormatter.getFormattedDayAndTime(model.snoozeTimeInMillis)

        return AlarmNotificationData(
            id = alarm.id,
            title = alarm.label.ifBlank { context.getString(R.string.snoozed_alarm) },
            contentText = formattedSnoozedTime,
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

