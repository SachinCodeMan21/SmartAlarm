package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult


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
     * @return [MyResult] A [MyResult.Success] if the alarm is successfully snoozed, or [MyResult.Error] if an error occurs.
     */
    suspend operator fun invoke(alarm: AlarmModel): MyResult<Unit, DataError>
}
