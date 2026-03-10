package com.example.smartalarm.feature.stopwatch.data.manager

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


/**
 * Thread-safe implementation of [StopwatchInMemoryStateManager].
 *
 * This implementation coordinates the data flow between the high-frequency Ticker
 * and the persistent Database. By caching state in a [StateFlow], it enables
 * a reactive UI without redundant database polling.
 */
class StopwatchInMemoryStateManagerImpl @Inject constructor() : StopwatchInMemoryStateManager {

    private val _state = MutableStateFlow(StopwatchModel())
    override val state: StateFlow<StopwatchModel> = _state.asStateFlow()

    override fun getCurrentState(): StopwatchModel = _state.value

    /**
     * Reconciles in-memory state with persistent storage.
     * Typically invoked by a synchronization manager during the app launch
     * sequence or after a confirmed database transaction.
     */
    override fun updateFromDatabase(dbModel: StopwatchModel) {
        _state.value = dbModel
    }

    /**
     * Updates state with ticker data only if the session is currently active.
     * * This check prevents "race condition" UI updates where a late ticker
     * pulse might override a 'Paused' or 'Stopped' state confirmed by the database.
     */
    override fun updateFromTicker(updatedStopwatch: StopwatchModel) {
        if (_state.value.isRunning) {
            _state.value = updatedStopwatch
        }
    }
}