package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Use case for starting a timer.
 * Adjusts the start time and changes the timer state to RUNNING.
 */
fun interface StartTimerUseCase {
    /**
     * Starts the given timer if it's not already running.
     *
     * @param timer The [TimerModel] to start.
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend operator fun invoke(timer: TimerModel): Result<Unit>
}


///**
// * Use case for starting a timer.
// * Adjusts the start time and changes the timer state to RUNNING.
// */
//fun interface StartTimerUseCase {
//    /**
//     * Starts the given timer if it's not already running.
//     *
//     * @param timer The [TimerModel] to start.
//     * @return A [Result] containing the updated [TimerModel] on success, or an error on failure.
//     */
//    suspend operator fun invoke(timer: TimerModel): Result<TimerModel>
//}