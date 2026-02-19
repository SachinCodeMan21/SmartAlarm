package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result


/**
 * Defines the use case for snoozing an alarm.
 * This involves updating the alarm state, stopping ongoing alarm sounds and vibrations,
 * scheduling the next snooze, and posting a snooze notification.
 */
interface SnoozeAlarmUseCase {

    /**
     * Snoozes the alarm by updating its state and scheduling the next snooze.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] if the alarm is successfully snoozed, or [Result.Error] if an error occurs.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<Unit>
}
