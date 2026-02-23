package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Use case for snoozing a timer.
 * Adds a fixed snooze duration to the remaining time and marks the timer as snoozed.
 */
fun interface SnoozeTimerUseCase {
    /**
     * Snoozes the given timer by extending its remaining time.
     *
     * @param timer The [TimerModel] to snooze.
     * @return A [MyResult] indicating success or failure of the operation.
     */
    suspend operator fun invoke(timer: TimerModel): MyResult<Unit, DataError>
}