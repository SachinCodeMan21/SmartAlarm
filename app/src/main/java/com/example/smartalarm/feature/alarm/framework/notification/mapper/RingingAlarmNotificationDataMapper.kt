package com.example.smartalarm.feature.alarm.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.framework.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.broadcasts.receivers.AlarmReceiver
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity
import javax.inject.Inject


/**
 * Maps [AlarmNotificationModel.RingingAlarmModel] into [AlarmNotificationData] for scheduled alarm notifications.
 *
 * This class constructs the notification data for alarms that are currently ringing or about to ring,
 * including:
 * - The alarm title and scheduled time.
 * - Optional "Snooze" action (if allowed).
 * - A "Stop" or "Complete Task" action depending on whether the alarm has missions.
 * - A content intent to open [AlarmActivity] when the user taps the notification.
 *
 * Annotated with `@Singleton` to ensure a single instance is used across the application.
 *
 * @constructor Injects the mapper instance via dependency injection.
 */
class RingingAlarmNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<AlarmNotificationModel.RingingAlarmModel, AlarmNotificationData> {

    /**
     * Maps a [AlarmNotificationModel.RingingAlarmModel] into an [AlarmNotificationData] object.
     *
     * Constructs the alarm title, formatted time, actions (e.g., snooze, stop/complete), and content intent.
     *
     * @param context The Android [Context] used to access resources and create intents.
     * @param model The scheduled alarm model to be mapped into notification data.
     * @return The [AlarmNotificationData] representing the UI for the scheduled alarm.
     */
    override fun map(context: Context, model: AlarmNotificationModel.RingingAlarmModel): AlarmNotificationData {
        val alarm = model.alarm
        val label = context.getString(R.string.scheduled_alarm)

        return AlarmNotificationData(
            id = alarm.id,
            title = label,
            contentText = timeFormatter.getFormattedDayAndTime(model.alarm.time.hour, model.alarm.time.minute),
            actions = getScheduledAlarmActions(context, alarm),
            contentIntent = getCreateContentPendingIntent(context, alarm.id),
            fullScreenIntent = getFullScreenIntent(context, alarm.id)
        )
    }


    //-------------------------------------------------------
    //  Scheduled Alarm Notification Actions Getters
    //-------------------------------------------------------

    /**
     * Builds the list of notification actions for a scheduled alarm.
     *
     * Adds:
     * - A "Snooze" action if snooze is enabled and under the snooze limit.
     * - A "Stop" or "Complete Task" action depending on whether the alarm has active missions.
     *
     * @param context The context used to access string resources and create pending intents.
     * @param alarm The [AlarmModel] containing snooze settings and mission data.
     * @return A list of [NotificationAction]s to be shown in the notification.
     */
    private fun getScheduledAlarmActions(context: Context, alarm: AlarmModel): List<NotificationAction> {

        val alarmActions = mutableListOf<NotificationAction>()

        // Stop or Complete Task
        val stopOrCompleteMissionTitle = context.getString(
            if (alarm.missions.isNotEmpty()) R.string.complete_mission else R.string.stop
        )
        val stopOrCompleteMissionPendingIntent = if (alarm.missions.isNotEmpty()) {
            getCreateContentPendingIntent(context,alarm.id)
        } else{
            getCreateStopPendingIntent(context, alarm.id)
        }



        alarmActions.add(
            NotificationAction(
                id = alarm.id + 100,
                title ="${alarm.snoozeSettings.snoozedCount} Snooze Left",
                icon = R.drawable.ic_alarm,
                pendingIntent = getCreateSnoozePendingIntent(context, alarm.id)
            )
        )

        alarmActions.add(
            NotificationAction(
                id = alarm.id + 200,
                title = stopOrCompleteMissionTitle,
                icon = R.drawable.ic_delete,
                pendingIntent = stopOrCompleteMissionPendingIntent
            )
        )

        return alarmActions
    }



    //-------------------------------------------------------
    //  Scheduled Alarm Notification PendingIntent Getters
    //-------------------------------------------------------
    /**
     * Creates a [PendingIntent] to snooze the alarm when the snooze action is tapped.
     *
     * @param context The context used to create the broadcast intent.
     * @param alarmId The unique identifier of the alarm.
     * @return A [PendingIntent] that triggers the snooze alarm action.
     */
    private fun getCreateSnoozePendingIntent(context: Context, alarmId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_SNOOZE
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId + 100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a [PendingIntent] to stop or complete the alarm when the stop action is tapped.
     *
     * @param context The context used to create the broadcast intent.
     * @param alarmId The unique identifier of the alarm.
     * @return A [PendingIntent] that triggers the stop alarm action.
     */
    private fun getCreateStopPendingIntent(context: Context, alarmId: Int): PendingIntent {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_STOP
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId + 200,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a [PendingIntent] to launch [AlarmActivity] when the notification is tapped.
     *
     * @param context The context used to create the activity intent.
     * @param alarmId The ID of the alarm to pass to the activity.
     * @return A [PendingIntent] that opens the alarm activity.
     */
    private fun getCreateContentPendingIntent(context: Context, alarmId: Int): PendingIntent {
        val intent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmKeys.ALARM_ID, alarmId)

        }
        return PendingIntent.getActivity(
            context,
            alarmId + 300,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a [PendingIntent] that launches [AlarmActivity] in full-screen mode.
     *
     * This intent is used to wake up the screen and display the alarm UI prominently.
     *
     * @param context The context used to create the intent.
     * @param alarmId The ID of the alarm to be passed to the activity.
     * @return A [PendingIntent] that launches the alarm activity in full-screen.
     */
    private fun getFullScreenIntent(context: Context, alarmId: Int): PendingIntent {

        val fullScreenIntent = Intent(context.applicationContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }

        return PendingIntent.getActivity(
            context,
            alarmId + 100,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    }

}

