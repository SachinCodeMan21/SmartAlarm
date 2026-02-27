package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DismissAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject

/**
 * Implementation of the [DismissAlarmUseCase] that handles the dismissal of an alarm.
 *
 * This class is responsible for updating the state of an alarm when it is dismissed,
 * either marking it as expired or rescheduling it based on its type (one-time or repeating).
 *
 * The process includes the following steps:
 * 1. Retrieving the alarm by its ID.
 * 2. Cancelling any active or upcoming notifications for the alarm.
 * 3. Depending on the alarm's state and type:
 *    - **For one-time alarms**:
 *      - Cancels the alarm and marks it as expired.
 *    - **For repeating alarms**:
 *      - Reschedules the next occurrence and resets the snooze settings if the alarm was snoozed.
 * 4. Persisting the updated alarm state in the database.
 *
 * This class is typically used when the user manually dismisses an alarm or when it needs to be automatically handled.
 *
 * @param getAlarmByIdUseCase The use case for retrieving an alarm by its ID.
 * @param updateAlarmUseCase The use case for updating the alarm in the database.
 * @param alarmScheduler The scheduler used for cancelling and rescheduling alarms.
 * @param alarmNotificationManager The manager responsible for cancelling notifications related to the alarm.
 * @param alarmTimeHelper Helper for calculating the next alarm time for repeating alarms.
 */
class DismissAlarmUseCaseImpl @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper
) : DismissAlarmUseCase {

    /**
     * Dismisses the alarm by updating its state, canceling any scheduled alarms or notifications,
     * and rescheduling if needed.
     *
     * - For one-time alarms: cancels the alarm and marks it as expired.
     * - For repeating alarms: reschedules the next occurrence and resets the snooze state if snoozed.
     *
     * @param alarmId The ID of the alarm to be dismissed.
     * @return The updated [AlarmModel] after the operation, or null if there was an error.
     */
    override suspend operator fun invoke(alarmId: Int): AlarmModel? {

        val alarm = getAlarm(alarmId) ?: return null

        val isOneTimeAlarm = alarm.days.isEmpty()

        // Cancel any upcoming/snoozed active notifications for the passed alarm id
        alarmNotificationManager.cancelAlarmNotification(alarm.id)

        val updatedAlarm = when (alarm.alarmState) {

            AlarmState.UPCOMING -> {
                if (isOneTimeAlarm) {
                    // One-time alarm: Cancel the scheduled alarm and mark it as expired
                    alarmScheduler.cancelSmartAlarm(alarm.id)
                    alarm.copy(isEnabled = false, alarmState = AlarmState.EXPIRED)
                } else {
                    // Repeating alarm: Reschedule the next alarm
                    val nextAlarmTime = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)
                    alarmScheduler.scheduleSmartAlarm(alarm.id, nextAlarmTime)
                    alarm.copy(isEnabled = true, alarmState = AlarmState.UPCOMING)
                }
            }

            AlarmState.SNOOZED -> {
                if (isOneTimeAlarm) {
                    // One-time alarm: Stop snooze and mark it as expired
                    alarmScheduler.cancelSnoozeAlarm(alarm.id)
                    alarm.copy(
                        isEnabled = false,
                        snoozeSettings = alarm.snoozeSettings.copy(
                            isAlarmSnoozed = false,
                            snoozedCount = alarm.snoozeSettings.snoozeLimit
                        ),
                        alarmState = AlarmState.EXPIRED
                    )
                } else {
                    // Repeating alarm: Cancel snooze, reset snooze state, and reschedule
                    alarmScheduler.cancelSnoozeAlarm(alarm.id)
                    val nextAlarmTime = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)
                    alarmScheduler.scheduleSmartAlarm(alarm.id, nextAlarmTime)

                    alarm.copy(
                        isEnabled = true,
                        snoozeSettings = alarm.snoozeSettings.copy(
                            isAlarmSnoozed = false,
                            snoozedCount = 0
                        ),
                        alarmState = AlarmState.UPCOMING
                    )
                }
            }

            else -> null // No action needed for other states
        }

        updatedAlarm?.let {
            updateAlarmUseCase(it) // Persist the updated alarm state
        }

        return updatedAlarm

    }

    /**
     * Retrieves the alarm associated with the given ID using [getAlarmByIdUseCase].
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return The [AlarmModel] if found, or `null` otherwise.
     */
    private suspend fun getAlarm(alarmId: Int): AlarmModel? {
        val result = getAlarmByIdUseCase(alarmId)
        return when (result) {
            is MyResult.Success -> result.data
            is MyResult.Error -> null
        }
    }
}
