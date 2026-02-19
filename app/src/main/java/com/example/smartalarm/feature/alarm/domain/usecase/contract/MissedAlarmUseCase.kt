package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.impl.MissedAlarmUseCaseImpl


/**
 * Defines the use case for marking an alarm as missed.
 * This involves updating the alarm state, resetting snooze settings, scheduling the next occurrence
 * for repeating alarms, and posting a missed alarm notification.
 */
interface MissedAlarmUseCase {

    /**
     * Marks the alarm as missed by updating its state and resetting necessary settings.
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] if the alarm is successfully marked as missed, or [Result.Error] if an error occurs.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<Unit>
}
