package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UndoAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
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
) : UndoAlarmUseCase {

    /**
     * Restores the alarm by saving it and scheduling it if enabled.
     *
     * - Attempts to save the alarm.
     * - If saving succeeds, the alarm is scheduled.
     * - If saving fails, an error message is returned.
     *
     * @param undoAlarm The alarm model that needs to be restored.
     * @return A [MyResult] containing either a success message or an error message.
     */
    override suspend fun invoke(undoAlarm: AlarmModel): MyResult<Unit, DataError> {
        return when (val result = saveAlarmUseCase(undoAlarm)) {
            is MyResult.Success -> {
                // If saving is successful, schedule the alarm and return success.
                scheduleAlarm(undoAlarm.copy(id = result.data))
                MyResult.Success(Unit)
            }
            is MyResult.Error -> {
                // If saving fails, return an error message.
                //Result.Error(Exception(resourceProvider.getString(R.string.error_failed_to_restore_alarm)))
                MyResult.Error(result.error)
            }
        }
    }

    /**
     * Schedules the alarm if it is enabled.
     *
     * - Calculates the next time for the alarm to trigger.
     * - Schedules the alarm using the scheduler and posts a notification.
     *
     * @param alarm The alarm model to be scheduled.
     * @return A [MyResult] containing the success message with the formatted alarm trigger time.
     */
    private fun scheduleAlarm(alarm: AlarmModel): String {
        if (!alarm.isEnabled) return ""

        val nextAlarmTime = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)
        alarmSchedular.scheduleSmartAlarm(alarm.id, nextAlarmTime)
        alarmNotificationManager.postAlarmNotification(
            alarm.id,
            AlarmNotificationModel.UpcomingAlarmModel(alarm, nextAlarmTime)
        )

        // Return the formatted string of the next alarm time
        return alarmTimeHelper.getFormattedTimeUntilNextAlarm(nextAlarmTime)
    }
}