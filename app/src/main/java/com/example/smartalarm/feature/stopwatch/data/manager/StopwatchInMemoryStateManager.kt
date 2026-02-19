package com.example.smartalarm.feature.stopwatch.data.manager

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing the in-memory state of the stopwatch.
 */
interface StopwatchInMemoryStateManager {

    /**
     * The current observable state of the stopwatch.
     */
    val state: StateFlow<StopwatchModel>

    /**
     * Retrieves the current state of the stopwatch.
     * @return The current [StopwatchModel].
     */
    fun getCurrentState(): StopwatchModel

    /**
     * Updates the stopwatch state from the database.
     * Only applies changes if the stopwatch status (running/paused) or lap count has changed.
     * @param dbModel The [StopwatchModel] from the database.
     */
    fun updateFromDatabase(dbModel: StopwatchModel)

    /**
     * Updates the stopwatch state with real-time data from the ticker (e.g., elapsed time, laps).
     * @param updatedStopwatch The updated [StopwatchModel] from the ticker.
     */
    fun updateFromTicker(updatedStopwatch: StopwatchModel)
}
