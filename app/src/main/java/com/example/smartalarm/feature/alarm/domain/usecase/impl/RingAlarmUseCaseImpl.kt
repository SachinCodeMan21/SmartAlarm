package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.exception.ExceptionMapper
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.RingAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Use case for ringing an alarm and handling the associated processes.
 * This includes playing the alarm sound, enabling vibration (if required),
 * updating the alarm state, and scheduling a timeout.
 *
 * @constructor Creates a [RingAlarmUseCaseImpl] instance with necessary dependencies.
 *
 * @property updateAlarmUseCase Use case for updating alarm details.
 * @property alarmRingtoneManager Manages alarm ringtone playback.
 * @property vibrationManager Manages vibration functionality.
 * @property alarmScheduler Schedules the alarm timeout.
 * @property alarmNotificationManager Manages alarm notifications.
 * @property sharedPrefsHelper Helper for saving shared preferences like last active alarm.
 */
class RingAlarmUseCaseImpl @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmRingtoneManager: AlarmRingtoneManager,
    private val vibrationManager: VibrationManager,
    private val alarmScheduler: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val sharedPrefsHelper: SharedPrefsHelper
) : RingAlarmUseCase {

    /**
     * Rings the alarm by updating its state to "RINGING", playing the alarm sound,
     * enabling vibration (if required), and scheduling the alarm timeout.
     *
     * @param alarm The [AlarmModel] object containing alarm details.
     * @return [Result] A [Result.Success] containing the updated [AlarmModel] if successful, or [Result.Error] if an error occurs.
     */
    override suspend fun invoke(alarm: AlarmModel): Result<AlarmModel> {
        return try {
            // Remove any existing upcoming or snoozed alarm notifications
            alarmNotificationManager.cancelAlarmNotification(alarm.id)

            // Update the alarm state to "RINGING"
            val updatedAlarm = alarm.copy(alarmState = AlarmState.RINGING)

            // Update the alarm state in the database
            when (val updateResult = updateAlarmUseCase(updatedAlarm)) {

                is Result.Success -> {

//                    // Play the alarm sound
//                    alarmRingtoneManager.playAlarmRingtone(alarm.alarmSound, alarm.volume)
//
//                    // Start vibration if enabled
//                    if (alarm.isVibrateEnabled) {
//                        vibrationManager.startVibration()
//                    }

                    // Schedule the alarm timeout after 1 minute
                    alarmScheduler.scheduleSmartAlarmTimeout(
                        alarm.id,
                        TimeUnit.MINUTES.toMillis(10) + 10000
                    )

                    // Save the ID of the last active alarm
                    //sharedPrefsHelper.lastActiveAlarmNotificationPref = alarm.id

                    // Return the updated alarm for the foreground notification
                    Result.Success(updatedAlarm)
                }

                is Result.Error -> {
                    Result.Error(updateResult.error)
                }
            }
        } catch (exception: Exception) {
            Result.Error(ExceptionMapper.map(exception))
        }
    }

}


