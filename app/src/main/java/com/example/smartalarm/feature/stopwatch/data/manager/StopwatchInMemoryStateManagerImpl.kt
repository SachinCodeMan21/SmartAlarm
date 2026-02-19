package com.example.smartalarm.feature.stopwatch.data.manager

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Implementation of [StopwatchInMemoryStateManager] that manages the stopwatch state in memory.
 */
class StopwatchInMemoryStateManagerImpl @Inject constructor() : StopwatchInMemoryStateManager {

    // Internal state flow representing the stopwatch state
    private val _state = MutableStateFlow(StopwatchModel())
    override val state: StateFlow<StopwatchModel> = _state.asStateFlow()

    /**
     * Retrieves the current stopwatch state.
     * @return The current [StopwatchModel].
     */
    override fun getCurrentState(): StopwatchModel = _state.value

    /**
     * Updates the stopwatch state from the database.
     * Applies changes if the stopwatch status (running/paused) or lap count has changed.
     * This helps prevent unnecessary updates while the stopwatch is running.
     *
     * @param dbModel The [StopwatchModel] from the database.
     */
    override fun updateFromDatabase(dbModel: StopwatchModel) {
        _state.value = dbModel
    }

    /**
     * Updates the stopwatch state with real-time data from the ticker (e.g., elapsed time and lap times).
     * Only updates the state if the stopwatch is currently running.
     *
     * @param updatedStopwatch The updated [StopwatchModel] from the ticker.
     */
    override fun updateFromTicker(updatedStopwatch: StopwatchModel) {
        // Only update if the stopwatch is running
        if (_state.value.isRunning) {
            _state.value = updatedStopwatch
        }
    }
}
