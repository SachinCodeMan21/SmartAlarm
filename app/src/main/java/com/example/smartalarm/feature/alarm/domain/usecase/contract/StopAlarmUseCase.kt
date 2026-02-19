package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result


/**
 * Defines the use case for stopping an alarm.
 * This involves updating the alarm state, resetting snooze settings,
 * and stopping any ongoing alarm sound or vibration.
 */
interface StopAlarmUseCase {

    /**
     * Stops the given alarm by updating its state to "EXPIRED", resetting snooze settings,
     * and stopping any ongoing alarm sound or vibration.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] if the alarm is successfully stopped, or [Result.Error] if an error occurs.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<Unit>
}
