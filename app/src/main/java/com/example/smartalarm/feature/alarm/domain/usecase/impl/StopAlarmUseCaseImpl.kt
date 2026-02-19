package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.usecase.contract.StopAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import javax.inject.Inject


/**
 * Implementation of the [StopAlarmUseCase] that handles the logic for stopping an alarm.
 * This includes updating the alarm state to "EXPIRED", resetting snooze settings, stopping
 * any ongoing alarm sounds or vibrations, and canceling scheduled timeouts.
 *
 * @constructor Creates an instance of [StopAlarmUseCaseImpl] with the necessary dependencies.
 *
 * @property updateAlarmUseCase Use case for updating alarm details.
 * @property alarmScheduler Schedules and cancels alarm timeouts.
 * @property alarmRingtoneHelper Manages stopping the alarm sound.
 * @property vibrationManager Manages stopping vibration.
 * @property sharedPrefsHelper Helps manage shared preferences like the last active alarm.
 */
class StopAlarmUseCaseImpl @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingtoneHelper: AlarmRingtoneManager,
    private val vibrationManager: VibrationManager,
    private val sharedPrefsHelper: SharedPrefsHelper,
) : StopAlarmUseCase {

    /**
     * Stops the given alarm by marking it as expired, resetting its snooze settings,
     * and stopping any ongoing alarm sound or vibration.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] if the alarm is successfully stopped, or [Result.Error] if an error occurs.
     */
    override suspend operator fun invoke(alarm: AlarmModel): Result<Unit> {

        return try {

            // Reset snooze settings
            val snoozeSettings = alarm.snoozeSettings.copy(
                isAlarmSnoozed = false,
                snoozedCount = alarm.snoozeSettings.snoozeLimit
            )

            // Prepare updated alarm state
            val updatedAlarm = alarm.copy(
                isEnabled = false,  // Disable the alarm
                snoozeSettings = snoozeSettings,
                alarmState = AlarmState.EXPIRED  // Mark as expired
            )

            // Update the alarm in the database
            when (val updateResult = updateAlarmUseCase(updatedAlarm)) {

                is Result.Success -> {
                    // Cancel any scheduled timeouts
                    alarmScheduler.cancelSmartAlarmTimeout(alarm.id)

                    // Stop the alarm sound
                    alarmRingtoneHelper.stopAlarmRingtone()
                    vibrationManager.stopVibration()

                    // Reset the last active alarm notification preference
                    sharedPrefsHelper.lastActiveAlarmNotificationPref = 0

                    // Return success result
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    // Return error result if alarm update fails
                    Result.Error(updateResult.exception)
                }

            }

        } catch (exception: Exception) {
            // Catch any unexpected exception and return it as Result.Error
            Result.Error(exception)
        }
    }
}
