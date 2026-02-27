package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SnoozeAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject



/**
 * Implementation of the [SnoozeAlarmUseCase] that handles the logic for snoozing an alarm.
 * This includes updating the alarm state, stopping ongoing alarm sounds, scheduling the next snooze,
 * and posting a notification for the snoozed alarm.
 *
 * @constructor Creates an instance of [SnoozeAlarmUseCaseImpl] with the necessary dependencies.
 *
 * @property updateAlarmUseCase Use case for updating alarm details.
 * @property alarmScheduler Schedules and cancels alarm timeouts.
 * @property alarmRingtoneHelper Manages stopping the alarm sound.
 * @property vibrationManager Manages stopping vibration.
 * @property alarmNotificationManager Manages alarm notifications.
 * @property alarmTimeHelper Provides utilities for calculating snooze time.
 * @property sharedPrefsHelper Helps manage shared preferences like last active alarm.
 */
class SnoozeAlarmUseCaseImpl @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val alarmRingtoneHelper: AlarmRingtoneManager,
    private val vibrationManager: VibrationManager,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper,
    private val sharedPrefsHelper: SharedPrefsHelper
) : SnoozeAlarmUseCase {

    /**
     * Snoozes the given alarm by updating its state, stopping ongoing sounds and vibrations,
     * scheduling the next snooze, and posting a snooze notification.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [MyResult] A [MyResult.Success] if the alarm is successfully snoozed, or [MyResult.Error] if an error occurs.
     */
    override suspend fun invoke(alarm: AlarmModel): MyResult<Unit, DataError> {
        // Create an updated snoozed alarm with the new state and snooze settings
        val updatedSnoozedAlarm = alarm.copy(
            snoozeSettings = alarm.snoozeSettings.copy(
                isAlarmSnoozed = true,
                snoozedCount = alarm.snoozeSettings.snoozedCount - 1
            ),
            alarmState = AlarmState.SNOOZED
        )

        return when (val updateResult = updateAlarmUseCase(updatedSnoozedAlarm)) {

            is MyResult.Success -> {

                // Stop any ongoing alarm sound and cancel any existing timeouts
//                    alarmRingtoneHelper.stopAlarmRingtone()
//                    vibrationManager.stopVibration()
                alarmScheduler.cancelSmartAlarmTimeout(alarm.id)

                // Schedule the next snooze based on the snooze interval
                val snoozeTimeInMillis =
                    alarmTimeHelper.getNextSnoozeMillis(alarm.snoozeSettings.snoozeIntervalMinutes)
                alarmScheduler.scheduleSnoozeAlarm(updatedSnoozedAlarm.id, snoozeTimeInMillis)

                // Post the snooze notification for the user
                alarmNotificationManager.postAlarmNotification(
                    updatedSnoozedAlarm.id,
                    AlarmNotificationModel.SnoozedAlarmModel(
                        updatedSnoozedAlarm,
                        snoozeTimeInMillis
                    )
                )

                // Reset the last active alarm notification preference
                //sharedPrefsHelper.lastActiveAlarmNotificationPref = 0

                // Return success result
                MyResult.Success(Unit)
            }

            is MyResult.Error -> {
                // Return error if alarm update fails
                MyResult.Error(updateResult.error)
            }
        }
    }
}
