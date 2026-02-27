package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SwipedAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
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
    ): MyResult<Unit, DataError> {
        return when (val result = deleteAlarmUseCase(swipedAlarmId)) {
            is MyResult.Success ->{
                cancelAlarm(swipedAlarmId, alarmState)  // If delete is successful, proceed to cancel
                MyResult.Success(Unit)
            }
            is MyResult.Error -> {
                MyResult.Error(result.error)
            }
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
     * @return A [MyResult] indicating success or failure of the cancellation.
     */
    private fun cancelAlarm(alarmId: Int, alarmState: AlarmState) {
        alarmSchedular.cancelAllScheduledAlarms(alarmId)

        if (alarmState == AlarmState.RINGING) {
            sharedPrefsHelper.lastActiveAlarmNotificationPref = 0
            alarmServiceController.stopAlarmService()
            alarmRingtoneManager.stopAlarmRingtone()
            vibrationManager.stopVibration()
        } else {
            alarmNotificationManager.cancelAlarmNotification(alarmId)
        }
    }
}
