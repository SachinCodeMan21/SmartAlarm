package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.UpdateStopwatchTickerStateUseCase
import javax.inject.Inject

/**
 * Implementation of [UpdateStopwatchTickerStateUseCase] that updates the in-memory stopwatch state.
 *
 * This use case calculates the elapsed time and updates the stopwatch state, including lap times.
 * It is typically called by the ticker job to keep the stopwatch state updated in real time.
 *
 * @param repository The [StopWatchRepository] used for updating the in-memory stopwatch state.
 * @param clockProvider Provides the current system time for calculating the elapsed time.
 */
class UpdateStopwatchTickerStateUseCaseImpl @Inject constructor(
    private val repository: StopWatchRepository,
    private val clockProvider: SystemClockHelper
) : UpdateStopwatchTickerStateUseCase {

    /**
     * Updates the in-memory stopwatch state with the latest elapsed time and lap data.
     *
     * This method calculates the elapsed time since the stopwatch started, updates the lap times,
     * and persists the new state in memory. It does nothing if the stopwatch is not running.
     */
    override fun invoke() {

        val current = repository.getCurrentStopwatchState()

        // Do nothing if the stopwatch isn't running
        if (!current.isRunning) return

        // Calculate the current elapsed time
        val elapsedTime = clockProvider.getCurrentTime() - current.startTime

        // Update the last lap's elapsed time and end time if there are laps
        val updatedLaps = current.lapTimes.toMutableList().apply {
            if (isNotEmpty()) {
                val lastLap = last()
                set(
                    lastIndex,
                    lastLap.copy(
                        lapElapsedTime = elapsedTime - lastLap.lapStartTime,
                        lapEndTime = elapsedTime
                    )
                )
            }
        }

        // Build the updated stopwatch model
        val updatedStopwatch = current.copy(
            elapsedTime = elapsedTime,
            endTime = elapsedTime,
            lapTimes = updatedLaps
        )

        // Update the in-memory state
        repository.updateTickerState(updatedStopwatch)
    }
}
