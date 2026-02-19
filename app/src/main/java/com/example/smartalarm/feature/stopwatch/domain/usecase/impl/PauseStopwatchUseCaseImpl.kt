package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.PauseStopwatchUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.extension.runCatchingResult
import javax.inject.Inject

/**
 * Implementation of [PauseStopwatchUseCase] that pauses a running stopwatch.
 *
 * Pauses the stopwatch if running, or returns the current state if already paused.
 */
class PauseStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopWatchRepository
) : PauseStopwatchUseCase {

    /**
     * Pauses the stopwatch if running and persists the updated state.
     *
     * Returns the current state if already paused.
     */
    override suspend fun invoke(): Result<Unit> {
        val stopwatch = repository.getCurrentStopwatchState()

        if (!stopwatch.isRunning) return Result.Success(Unit)

        val paused = stopwatch.copy(isRunning = false)

        return runCatchingResult {
            repository.persistStopwatch(paused)
        }
    }
}
