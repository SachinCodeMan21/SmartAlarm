package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.exception.ExceptionMapper
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.ToggleAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject


/**
 * Implementation of [ToggleAlarmUseCase] for toggling an alarm's enabled state.
 *
 * This implementation handles updating the alarm's state, scheduling or canceling the alarm,
 * and posting or canceling notifications based on whether the alarm is enabled or disabled.
 */
class ToggleAlarmUseCaseImpl @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmSchedular: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper
) : ToggleAlarmUseCase {

    /**
     * Toggles the alarm's enabled state.
     *
     * - Updates the alarm's state.
     * - Schedules the alarm if it is enabled, or cancels the alarm if it is disabled.
     *
     * @param alarmModel The current alarm model.
     * @param isEnabled A boolean indicating whether the alarm should be enabled or disabled.
     * @return A [Result] containing a success message or an error if the operation fails.
     */
    override suspend fun invoke(alarmModel: AlarmModel, isEnabled: Boolean): Result<String> {

        // Create a copy of the current alarm with the updated enabled state and alarm state.
        val updatedAlarm = alarmModel.copy(
            isEnabled = isEnabled,
            snoozeSettings = alarmModel.snoozeSettings.copy(isAlarmSnoozed = false, snoozedCount = alarmModel.snoozeSettings.snoozeLimit),
            alarmState = if (isEnabled) AlarmState.UPCOMING else AlarmState.EXPIRED
        )

        // Attempt to update the alarm and schedule or cancel based on the updated state.
        return when (val result = updateAlarmUseCase(updatedAlarm)) {
            is Result.Success -> if (updatedAlarm.isEnabled) scheduleAlarm(updatedAlarm) else cancelAlarm(updatedAlarm)
            is Result.Error -> Result.Error(result.error)
        }
    }

    /**
     * Schedules the alarm if it is enabled.
     *
     * - Calculates the remaining time for the alarm to trigger.
     * - Schedules the alarm and posts a notification.
     *
     * @param alarm The alarm model to be scheduled.
     * @return A [Result] containing the success message with the formatted alarm trigger time.
     */
    private fun scheduleAlarm(alarm: AlarmModel): Result<String> {
        return try {
            if (alarm.isEnabled) {

                // Calculate the time until the alarm triggers.
                val remainingTimeMillis = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)

                // Schedule the alarm and post a notification.
                alarmSchedular.scheduleSmartAlarm(alarm.id, remainingTimeMillis)
                alarmNotificationManager.postAlarmNotification(alarm.id, AlarmNotificationModel.UpcomingAlarmModel(alarm, remainingTimeMillis))

                // Return a success message with the formatted alarm trigger time.
                Result.Success(alarmTimeHelper.getFormattedTimeUntilNextAlarm(remainingTimeMillis))
            } else {
                Result.Success("")  // Return an empty string if the alarm is disabled.
            }
        } catch (exception: Exception) {
            Result.Error(ExceptionMapper.map(exception))  // Return error in case of failure.
        }
    }

    /**
     * Cancels the alarm if it is disabled.
     *
     * - Cancels any scheduled alarms.
     * - Cancels any existing notifications for the alarm.
     *
     * @param alarm The alarm model to be canceled.
     * @return A [Result] containing an empty success message if canceled successfully.
     */
    private fun cancelAlarm(alarm: AlarmModel): Result<String> {
        return try {
            // Cancel all scheduled alarms related to this alarm ID.
            alarmSchedular.cancelAllScheduledAlarms(alarm.id)

            // Cancel any existing notifications unless the alarm is currently ringing.
            if (alarm.alarmState != AlarmState.RINGING) {
                alarmNotificationManager.cancelAlarmNotification(alarm.id)
            }

            Result.Success("")  // Return an empty string upon successful cancellation.
        } catch (exception: Exception) {
            Result.Error(ExceptionMapper.map(exception))  // Return error in case of failure.
        }
    }
}