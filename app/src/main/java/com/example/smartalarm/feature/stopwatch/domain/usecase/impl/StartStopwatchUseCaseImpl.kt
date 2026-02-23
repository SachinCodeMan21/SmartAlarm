package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.StartStopwatchUseCase
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import javax.inject.Inject

/**
 * Implementation of [StartStopwatchUseCase] that starts the stopwatch by setting the start time
 * based on the current system elapsed real time, and persists the updated state via the repository.
 *
 * @param clockProvider Provides the current system time in milliseconds for the stopwatch start time.
 * @param repository The [StopwatchRepository] used to save the updated stopwatch state.
 */
class StartStopwatchUseCaseImpl @Inject constructor(
    private val clockProvider: SystemClockHelper,
    private val repository: StopwatchRepository
) : StartStopwatchUseCase {

    /**
     * Starts the stopwatch if not already running and persists the updated state.
     *
     * Sets the start time based on the current system time and updates the stopwatch state.
     * If the stopwatch is already running, returns the current state without changes.
     *
     * @return A [MyResult] containing the updated [StopwatchModel] on success, or an error if the operation fails.
     */
    override suspend fun invoke(): MyResult<Unit, DataError> {
        val stopwatch = repository.getCurrentStopwatchState()

        // If the stopwatch is already running, no update is needed
        if (stopwatch.isRunning) return MyResult.Success(Unit)

        // Update the stopwatch state to reflect it being started
        val updated = stopwatch.copy(
            startTime = clockProvider.getCurrentTime() - stopwatch.elapsedTime,
            isRunning = true
        )

        // Persist the updated state
        return repository.persistStopwatch(updated)
    }
}
