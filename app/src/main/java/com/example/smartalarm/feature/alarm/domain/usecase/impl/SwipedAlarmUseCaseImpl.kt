package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.R
import com.example.smartalarm.core.exception.ExceptionMapper
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SwipedAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import javax.inject.Inject



/**
 * Implementation of [SwipedAlarmUseCase] for handling a swiped alarm action, such as deleting and canceling scheduled alarms.
 *
 * This class encapsulates the logic for deleting an alarm from the repository, canceling any scheduled alarms, and stopping the alarm if it's currently ringing.
 */
class SwipedAlarmUseCaseImpl @Inject constructor(
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val alarmSchedular: AlarmScheduler,
    private val alarmRingtoneManager: AlarmRingtoneManager,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val vibrationManager: VibrationManager,
    private val alarmServiceController: AlarmServiceController,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val resourceProvider: ResourceProvider
) : SwipedAlarmUseCase {

    /**
     * Handles the swipe action for an alarm.
     *
     * 1. Deletes the alarm using the `DeleteAlarmUseCase`.
     * 2. Cancels the alarm if it's scheduled or active.
     *
     * @param swipedAlarmId The ID of the alarm that was swiped.
     * @param alarmState The state of the alarm (e.g., RINGING, INACTIVE, etc.).
     * @return A [Result] indicating whether the operation was successful or failed.
     */
    override suspend fun invoke(
        swipedAlarmId: Int,
        alarmState: AlarmState
    ): Result<Unit> {
        return when (val result = deleteAlarmUseCase(swipedAlarmId)) {
            is Result.Success -> cancelAlarm(swipedAlarmId, alarmState)  // If delete is successful, proceed to cancel
            is Result.Error -> Result.Error(
             //   Exception(resourceProvider.getString(R.string.error_failed_to_delete_alarm))
                result.error
            ) // If delete fails, return error
        }
    }

    /**
     * Cancels the alarm by stopping its scheduled tasks and clearing any active alarm states.
     *
     * 1. Cancels all scheduled alarms.
     * 2. If the alarm is ringing, stops the service, ringtone, and vibration.
     * 3. If the alarm is not ringing, cancels the associated notification.
     *
     * @param alarmId The ID of the alarm to be canceled.
     * @param alarmState The state of the alarm (e.g., RINGING, INACTIVE, etc.).
     * @return A [Result] indicating success or failure of the cancellation.
     */
    private fun cancelAlarm(
        alarmId: Int,
        alarmState: AlarmState
    ): Result<Unit> {
        return try {
            // Cancel all scheduled alarms (smart, snooze, timeout)
            alarmSchedular.cancelAllScheduledAlarms(alarmId)

            if (alarmState == AlarmState.RINGING) {
                // If the alarm is ringing, stop all associated actions (service, ringtone, vibration)
                sharedPrefsHelper.lastActiveAlarmNotificationPref = 0
                alarmServiceController.stopAlarmService()
                alarmRingtoneManager.stopAlarmRingtone()
                vibrationManager.stopVibration()
            } else {
                // If the alarm is not ringing, simply cancel the notification
                alarmNotificationManager.cancelAlarmNotification(alarmId)
                //alarmNotificationManager.cancelNotification(alarmId)
            }

            // Return success after cancellation
            Result.Success(Unit)
        } catch (exception: Exception) {
            // Return an error result in case of failure
            Result.Error(ExceptionMapper.map(exception))
        }
    }
}
