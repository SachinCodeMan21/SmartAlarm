package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.core.exception.MyResult

/**
 * Use case for starting a timer.
 * Adjusts the start time and changes the timer state to RUNNING.
 */
fun interface StartTimerUseCase {
    /**
     * Starts the given timer if it's not already running.
     *
     * @param timer The [TimerModel] to start.
     * @return A [MyResult] indicating success or failure of the operation.
     */
    suspend operator fun invoke(timer: TimerModel): MyResult<Unit, DataError>
}