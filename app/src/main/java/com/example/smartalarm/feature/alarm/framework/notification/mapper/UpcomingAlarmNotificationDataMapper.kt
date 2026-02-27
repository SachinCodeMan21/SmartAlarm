package com.example.smartalarm.feature.alarm.framework.notification.mapper

import android.content.Context
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.framework.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel.UpcomingAlarmModel
import com.example.smartalarm.feature.alarm.framework.notification.mapper.AlarmNotificationDataMapperHelper.createContentIntent
import com.example.smartalarm.feature.alarm.framework.notification.mapper.AlarmNotificationDataMapperHelper.createDismissIntent
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import javax.inject.Inject

/**
 * Maps an [AlarmNotificationModel.UpcomingAlarmModel] into [AlarmNotificationData] for
 * upcoming alarm notifications.
 *
 * This mapper constructs the data required to display a notification when an alarm
 * is upcoming. It handles:
 * - The notification title and formatted time for the upcoming alarm.
 * - A "Dismiss" action to allow the user to cancel the alarm.
 * - A [android.app.PendingIntent] to open the app when the notification is tapped.
 *
 * Implements [AppNotificationDataMapper] to integrate with the app’s notification
 * generation framework, allowing dynamic mapping of domain models to notification UI data.
 */
class UpcomingAlarmNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<UpcomingAlarmModel, AlarmNotificationData> {

    /**
     * Converts an [UpcomingAlarmModel] into a displayable [AlarmNotificationData] object.
     *
     * @param context The Android [Context] used for resource access and intent creation.
     * @param model The domain model containing alarm information.
     * @return A fully constructed [AlarmNotificationData] ready to be passed to
     *         a notification builder.
     */
    override fun map(context: Context, model: UpcomingAlarmModel): AlarmNotificationData {
        val alarm = model.alarm

        return AlarmNotificationData(
            id = alarm.id,
            title = context.getString(R.string.upcoming_alarm),
            contentText = timeFormatter.getFormattedDayAndTime(model.nextAlarmTimeInMillis),
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

