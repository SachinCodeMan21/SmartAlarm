package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.LapStopwatchUseCase
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
 * @property repository The [StopwatchRepository] for persisting the updated stopwatch state.
 * @property clockProvider A helper for providing the current system time used for lap calculations.
 */
class LapStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopwatchRepository,
    private val clockProvider: SystemClockHelper
) : LapStopwatchUseCase {

    /**
     * Records a lap for the running stopwatch and updates its state.
     * * **Why:** This encapsulates the business logic of calculating lap intervals
     * and ensures the new lap is persisted immediately to prevent data loss.
     */
    override suspend fun invoke(): MyResult<Unit, DataError> {
        val stopwatch = repository.getCurrentStopwatchState()

        // 1. Business Logic Validation
        // If not running, we don't record laps. We return Success because
        // "doing nothing" isn't a system failure, it's a skipped action.
        if (!stopwatch.isRunning) return MyResult.Success(Unit)

        // 2. State Calculation
        val currentTime = clockProvider.getCurrentTime() - stopwatch.startTime
        val updatedLaps = stopwatch.lapTimes.toMutableList()

        if (updatedLaps.isEmpty()) {
            updatedLaps.add(StopwatchLapModel(1, 0L, currentTime, currentTime))
        }

        updatedLaps.add(
            StopwatchLapModel(
                lapIndex = updatedLaps.size + 1,
                lapStartTimeMillis = currentTime,
                lapElapsedTimeMillis = 0L,
                lapEndTimeMillis = currentTime
            )
        )

        val updatedStopwatch = stopwatch.copy(
            lapTimes = updatedLaps,
            lapCount = updatedLaps.size
        )

        // 3. Delegation & Error Propagation
        return repository.persistStopwatch(updatedStopwatch)
    }
}