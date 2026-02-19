package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.MissedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject

/**
 * Implementation of the [MissedAlarmUseCase] that handles the logic for marking an alarm as missed.
 * This includes updating the alarm state, resetting snooze settings, scheduling repeating alarms,
 * canceling any smart alarm timeouts, and posting a notification for the missed alarm.
 *
 * @constructor Creates an instance of [MissedAlarmUseCaseImpl] with the necessary dependencies.
 *
 * @property updateAlarmUseCase Use case for updating alarm details.
 * @property alarmScheduler Schedules and cancels alarm timeouts.
 * @property alarmNotificationManager Manages alarm notifications.
 * @property alarmTimeHelper Provides utilities for calculating the next alarm time for repeating alarms.
 */
class MissedAlarmUseCaseImpl @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper,
    private val sharedPrefsHelper: SharedPrefsHelper
) : MissedAlarmUseCase {

    /**
     * Marks the given alarm as missed by updating its state, resetting snooze settings,
     * scheduling the next occurrence if it's a repeating alarm, and posting a missed alarm notification.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] if the alarm is successfully marked as missed, or [Result.Error] if an error occurs.
     */
    override suspend fun invoke(alarm: AlarmModel): Result<Unit> {

        return try {

            val isRepeatingAlarm = alarm.days.isNotEmpty()

            // Reset snooze settings when marking the alarm as missed
            val snoozeSettings = alarm.snoozeSettings.copy(
                isAlarmSnoozed = false,  // Reset snooze status
                snoozedCount = alarm.snoozeSettings.snoozeLimit  // Reset snooze count to limit
            )

            // Update the alarm state to "MISSED" and apply new settings
            val missedAlarm = alarm.copy(
                isEnabled = isRepeatingAlarm,  // Keep the alarm enabled if it's repeating
                snoozeSettings = snoozeSettings,
                alarmState = AlarmState.MISSED  // Update alarm state to "MISSED"
            )

            // Attempt to update the alarm in the database
            when (val updateResult = updateAlarmUseCase(missedAlarm)) {

                is Result.Success -> {
                    // If the alarm is repeating, schedule the next occurrence
                    if (isRepeatingAlarm) {
                        scheduleRepeatingAlarm(missedAlarm)
                    }

                    // Cancel any smart alarm timeout for the missed alarm
                    alarmScheduler.cancelSmartAlarmTimeout(missedAlarm.id)

                    // Post a notification indicating the alarm was missed
                    alarmNotificationManager.postAlarmNotification(
                        missedAlarm.id,
                        AlarmNotificationModel.MissedAlarmModel(missedAlarm)
                    )

                    sharedPrefsHelper.lastActiveAlarmNotificationPref = 0


                    // Return a success result
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    // Return an error result if the update fails
                    Result.Error(updateResult.exception)
                }
            }
        } catch (exception: Exception) {
            // Catch unexpected errors and return a Result.Error with the exception
            Result.Error(exception)
        }
    }

    /**
     * Schedules the next occurrence of a repeating alarm.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     */
    private fun scheduleRepeatingAlarm(alarm: AlarmModel) {
        // Calculate the next time the alarm should trigger
        val nextAlarmTime = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)

        // Schedule the next occurrence for the repeating alarm
        alarmScheduler.scheduleSmartAlarm(alarm.id, nextAlarmTime)
    }
}
