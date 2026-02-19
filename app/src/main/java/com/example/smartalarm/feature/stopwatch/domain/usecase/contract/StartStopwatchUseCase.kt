package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel

/**
 * Use case for starting a stopwatch.
 *
 * Starts the stopwatch if it is not already running and returns a [Result] containing the updated state.
 */
fun interface StartStopwatchUseCase {

    /**
     * Starts the stopwatch and returns the updated state in a [Result].
     *
     * Returns the updated [StopwatchModel] in a running state on success, or an error if starting or persisting the stopwatch fails.
     */
    suspend operator fun invoke(): Result<Unit>
}
