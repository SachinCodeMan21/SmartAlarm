package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.usecase.impl.SwipedAlarmUseCaseImpl


/**
 * Interface defining the use case for handling a swiped alarm action (such as deleting and canceling the alarm).
 *
 * This use case is responsible for deleting an alarm and canceling any associated scheduled tasks (e.g., stopping the ringtone, vibration, etc.).
 */
interface SwipedAlarmUseCase {

    /**
     * Executes the action of swiping (deleting and canceling) an alarm.
     *
     * @param swipedAlarmId The ID of the alarm that was swiped.
     * @param alarmState The current state of the alarm (e.g., RINGING or other states).
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(
        swipedAlarmId: Int,
        alarmState: AlarmState
    ): Result<Unit>
}

