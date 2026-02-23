package com.example.smartalarm.feature.stopwatch.data.manager

import com.example.smartalarm.feature.stopwatch.data.sync.StopwatchDbSyncManager
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


/**
 * Implementation of [StopwatchInMemoryStateManager] that acts as the centralized
 * state coordinator for the stopwatch feature.
 *
 * ### Why this exists:
 * This class serves as the bridge between transient, high-frequency data (Ticker)
 * and persistent, authoritative data (Database). By maintaining the state in
 * memory, it allows the Repository to provide a unified, reactive stream to
 * consumers without requiring constant database polling.
 */
class StopwatchInMemoryStateManagerImpl @Inject constructor() : StopwatchInMemoryStateManager {

    private val _state = MutableStateFlow(StopwatchModel())
    override val state: StateFlow<StopwatchModel> = _state.asStateFlow()

    /**
     * Provides an immediate snapshot of the current state to the Repository.
     * * ### Why:
     * This is used by the Repository to perform business logic checks or to
     * map the current model to an entity before persistence.
     */
    override fun getCurrentState(): StopwatchModel = _state.value

    /**
     * Synchronizes memory with the authoritative database record during app startup.
     * * ### Why:
     * This is called by the [StopwatchDbSyncManager] to ensure that any
     * changes persisted to disk (from this or other processes) are
     * reflected in the active memory state. It acts as the "reconciliation"
     * point for the reactive data loop.
     *
     * @param dbModel The model recently retrieved or updated in the local database.
     */
    override fun updateFromDatabase(dbModel: StopwatchModel) {
        _state.value = dbModel
    }

    /**
     * Updates memory with high-frequency snapshots from the ticker.
     * * ### Why:
     * The Repository uses this to push millisecond updates that are too
     * frequent to persist to disk. To maintain data integrity, we only
     * accept these updates if the session is active ([StopwatchModel.isRunning]),
     * ensuring memory doesn't deviate from the last saved "Paused" state.
     *
     * @param updatedStopwatch The high-frequency model emitted by the ticker logic.
     */
    override fun updateFromTicker(updatedStopwatch: StopwatchModel) {
        if (_state.value.isRunning) {
            _state.value = updatedStopwatch
        }
    }
}