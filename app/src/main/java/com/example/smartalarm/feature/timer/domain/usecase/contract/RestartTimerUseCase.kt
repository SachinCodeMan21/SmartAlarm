package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Use case for restarting a timer to its original target time.
 * Resets the timer while preserving its ID and start time.
 */
fun interface RestartTimerUseCase {
    /**
     * Restarts the given timer by resetting its remaining time to the target duration.
     *
     * @param timer The [TimerModel] to restart.
     * @return A [MyResult] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(timer: TimerModel): MyResult<Unit, DataError>
}
