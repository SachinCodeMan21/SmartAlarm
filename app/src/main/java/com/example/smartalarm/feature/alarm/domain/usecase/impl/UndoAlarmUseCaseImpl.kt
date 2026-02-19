package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UndoAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject


/**
 * Implementation of [UndoAlarmUseCase] for undoing an alarm's deleted state and rescheduling it.
 *
 * This implementation saves the alarm back into the system, schedules it if it's enabled, and posts a notification.
 */
class UndoAlarmUseCaseImpl @Inject constructor(
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val alarmSchedular: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper,
    private val resourceProvider: ResourceProvider
) : UndoAlarmUseCase {

    /**
     * Restores the alarm by saving it and scheduling it if enabled.
     *
     * - Attempts to save the alarm.
     * - If saving succeeds, the alarm is scheduled.
     * - If saving fails, an error message is returned.
     *
     * @param undoAlarm The alarm model that needs to be restored.
     * @return A [Result] containing either a success message or an error message.
     */
    override suspend fun invoke(undoAlarm: AlarmModel): Result<String> {
        return try {
            // Attempt to save the alarm back into the system.
            when (val result = saveAlarmUseCase(undoAlarm)) {
                is Result.Success -> {
                    // If saving is successful, schedule the alarm and return success.
                    scheduleAlarm(undoAlarm.copy(id = result.data))
                }
                is Result.Error -> {
                    // If saving fails, return an error message.
                    Result.Error(Exception(resourceProvider.getString(R.string.error_failed_to_restore_alarm)))
                }
            }
        } catch (_: Exception) {
            // Catch any unexpected errors and return an error result.
            Result.Error(Exception(resourceProvider.getString(R.string.error_failed_to_restore_alarm)))
        }
    }

    /**
     * Schedules the alarm if it is enabled.
     *
     * - Calculates the next time for the alarm to trigger.
     * - Schedules the alarm using the scheduler and posts a notification.
     *
     * @param alarm The alarm model to be scheduled.
     * @return A [Result] containing the success message with the formatted alarm trigger time.
     */
    private fun scheduleAlarm(alarm: AlarmModel): Result<String> {
        return try {
            if (alarm.isEnabled) {
                // Calculate the time for the next alarm trigger.
                val nextAlarmTime = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)

                // Schedule the alarm and post a notification.
                alarmSchedular.scheduleSmartAlarm(alarm.id, nextAlarmTime)
                alarmNotificationManager.postAlarmNotification(
                    alarm.id,
                    AlarmNotificationModel.UpcomingAlarmModel(alarm, nextAlarmTime)
                )

                // Return success with the formatted alarm trigger time.
                Result.Success(alarmTimeHelper.getFormattedTimeUntilNextAlarm(nextAlarmTime))
            } else {
                // If the alarm is not enabled, return a success result with an empty string.
                Result.Success("")
            }
        } catch (exception: Exception) {
            // If an error occurs during scheduling, return an error result.
            Result.Error(exception)
        }
    }
}