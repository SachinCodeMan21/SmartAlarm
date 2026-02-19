package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Use case for pausing a running timer.
 * Captures the current elapsed and remaining time, and updates the timer state to PAUSED.
 */
fun interface PauseTimerUseCase {
    /**
     * Pauses the given timer if it is currently running.
     *
     * @param timer The [TimerModel] to pause.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(timer: TimerModel): Result<Unit>
}



///**
// * Use case for pausing a running timer.
// * Captures the current elapsed and remaining time, and updates the timer state to PAUSED.
// */
//fun interface PauseTimerUseCase {
//    /**
//     * Pauses the given timer if it is currently running.
//     *
//     * @param timer The [TimerModel] to pause.
//     * @return A [Result] containing the updated [TimerModel] with paused state on success, or an error on failure.
//     */
//    suspend operator fun invoke(timer: TimerModel): Result<TimerModel>
//}