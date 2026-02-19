package com.example.smartalarm.feature.alarm.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.broadcasts.receivers.AlarmReceiver
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import java.time.LocalTime

/**
 * Utility object for creating [PendingIntent]s used in alarm-related actions.
 *
 * Provides helper methods to generate intents for:
 * - Launching the app's home screen when an alarm goes off.
 * - Dismissing alarms via broadcast receivers.
 * - Formatting [LocalTime] instances for display in notifications or UI.
 *
 * This object is typically used in alarm notification builders or alarm scheduling services.
 */
object AlarmNotificationDataMapperHelper {

    private const val REQUEST_CODE_OFFSET_DISMISS = 1000
    private const val REQUEST_CODE_OFFSET_CONTENT = 2000


    /**
     * Creates a [PendingIntent] to send a broadcast to [AlarmReceiver] for dismissing an alarm.
     *
     * This is typically used when the user dismisses an alarm from the notification.
     *
     * @param context The context used to create the intent.
     * @param alarmId Unique identifier for the alarm, used as the request code for the PendingIntent.
     * @return A [PendingIntent] that triggers [AlarmReceiver] with a dismiss action.
     */
    fun createDismissIntent(context: Context, alarmId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_DISMISS
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId + REQUEST_CODE_OFFSET_DISMISS,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a [PendingIntent] to launch [HomeActivity] when triggered.
     *
     * This intent is typically used to open the app when an alarm notification is tapped.
     * It clears any existing task and starts a new one.
     *
     * @param context The context used to create the intent.
     * @param alarmId Unique identifier for the alarm, used as the request code for the PendingIntent.
     * @return A [PendingIntent] that opens [HomeActivity].
     */
    fun createContentIntent(context: Context, alarmId: Int): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            alarmId + REQUEST_CODE_OFFSET_CONTENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}