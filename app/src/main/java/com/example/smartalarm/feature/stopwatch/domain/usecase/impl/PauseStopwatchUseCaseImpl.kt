package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.PauseStopwatchUseCase
import javax.inject.Inject

/**
 * Implementation of [PauseStopwatchUseCase] that pauses a running stopwatch.
 *
 * Pauses the stopwatch if running, or returns the current state if already paused.
 */
class PauseStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopwatchRepository
) : PauseStopwatchUseCase {

    /**
     * Pauses the stopwatch if running and persists the updated state.
     *
     * Returns the current state if already paused.
     */
    override suspend fun invoke(): MyResult<Unit, DataError> {

        val stopwatch = repository.getCurrentStopwatchState()

        // If the stopwatch is not running, return early
        if (!stopwatch.isRunning) return MyResult.Success(Unit)

        // Pause the stopwatch by updating the state
        val paused = stopwatch.copy(isRunning = false)

        // Persist the updated state
        return repository.persistStopwatch(paused)

    }
}
