package com.example.smartalarm.feature.alarm.framework.scheduler.factory

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.broadcasts.receivers.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Factory class responsible for creating [PendingIntent] instances for different alarm actions.
 *
 * This class centralizes the creation of PendingIntents used by the [android.app.AlarmManager] to:
 * - Trigger alarms.
 * - Handle alarm timeouts.
 * - Retrigger alarms for recurring or mission-based alarms.
 *
 * Each PendingIntent is uniquely identified by the alarm ID to prevent collisions.
 *
 * @property context The application context used to create intents.
 */
class AlarmIntentFactory @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    //---------------------------------------------------------------
    // Creating Different Pending Intent For Different Alarm Action
    //---------------------------------------------------------------
    /**
     * Creates a PendingIntent that triggers the main alarm action.
     *
     * @param alarmId The unique identifier of the alarm.
     * @return A [PendingIntent] that will broadcast the trigger alarm action.
     */
    fun createTriggerAlarmPendingIntent(alarmId: Int): PendingIntent {
        return createPendingIntent(alarmId, AlarmBroadCastAction.ACTION_TRIGGER)
    }

    /**
     * Creates a PendingIntent for the alarm timeout action.
     *
     * @param alarmId The unique identifier of the alarm.
     * @return A [PendingIntent] that will broadcast the timeout action.
     */
    fun createTimeoutAlarmPendingIntent(alarmId: Int): PendingIntent {
        return createPendingIntent(alarmId, AlarmBroadCastAction.ACTION_TIMEOUT)
    }

    /**
     * Creates a PendingIntent for retriggering an alarm, e.g., for missions or repeating alarms.
     *
     * @param alarmId The unique identifier of the alarm.
     * @return A [PendingIntent] that will broadcast the retrigger action.
     */
    fun createRetriggerPendingIntent(alarmId: Int): PendingIntent {
        return createPendingIntent(alarmId, AlarmBroadCastAction.ACTION_RETRIGGER)
    }



    //---------------------------------------------------------------
    // Helper Method For Creating Alarm Pending Intent
    //---------------------------------------------------------------

    /**
     * Internal helper to create a [PendingIntent] with the specified action for the given alarm ID.
     *
     * @param alarmId The unique identifier of the alarm.
     * @param action The action string for the intent.
     * @return A configured [PendingIntent] ready for scheduling with [android.app.AlarmManager].
     */
    private fun createPendingIntent(alarmId: Int, action: String): PendingIntent {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }

        return PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
