package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel


/**
 * Use case for pausing a running stopwatch.
 *
 * Pauses the stopwatch if running, or returns the current state if already paused.
 */
fun interface PauseStopwatchUseCase {

    /**
     * Pauses the stopwatch and returns the updated state in a [MyResult].
     *
     * Returns the current state if already paused, or an error if the operation fails.
     */
    suspend operator fun invoke(): MyResult<Unit, DataError>
}
