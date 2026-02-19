package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.LapStopwatchUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.extension.runCatchingResult
import javax.inject.Inject

/**
 * Implementation of [LapStopwatchUseCase] that handles recording laps for a running stopwatch.
 *
 * This use case handles the logic for recording a new lap when the stopwatch is running,
 * updating the lap timings, and persisting the updated stopwatch state.
 *
 * ### Behavior:
 * - If the stopwatch is not running, no lap is recorded, and the current state is returned.
 * - If the stopwatch is running, it calculates the elapsed time and records a new lap.
 * - Adds the new lap to the lap list, increments the lap count, and persists the updated state.
 *
 * @property repository The [StopWatchRepository] for persisting the updated stopwatch state.
 * @property clockProvider A helper for providing the current system time used for lap calculations.
 */
class LapStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopWatchRepository,
    private val clockProvider: SystemClockHelper
) : LapStopwatchUseCase {

    /**
     * Records a lap for the running stopwatch and updates its state.
     *
     * If the stopwatch is running, it calculates the current lap's elapsed time, adds a new lap entry,
     * updates the lap count, and persists the updated state. If the stopwatch is not running, no lap is added.
     *
     * @return A [Result] wrapping the success or failure of the persistence operation.
     */
    override suspend fun invoke(): Result<Unit> {

        val stopwatch = repository.getCurrentStopwatchState()

        // Return early if stopwatch is not running
        if (!stopwatch.isRunning) return Result.Success(Unit)

        val currentTime = clockProvider.getCurrentTime() - stopwatch.startTime
        val updatedLaps = stopwatch.lapTimes.toMutableList()

        // Add first lap if lap list is empty
        if (updatedLaps.isEmpty()) {
            updatedLaps.add(StopWatchLapModel(1, 0L, currentTime, currentTime))
        }

        // Add new lap
        updatedLaps.add(
            StopWatchLapModel(
                lapIndex = updatedLaps.size + 1,
                lapStartTime = currentTime,
                lapElapsedTime = 0L,
                lapEndTime = currentTime
            )
        )

        val updatedStopwatch = stopwatch.copy(
            lapTimes = updatedLaps,
            lapCount = updatedLaps.size
        )

        // Persist updated stopwatch
        return runCatchingResult {
            repository.persistStopwatch(updatedStopwatch)
        }
    }
}
