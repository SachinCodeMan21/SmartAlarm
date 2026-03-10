package com.example.smartalarm.feature.stopwatch.data.manager

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for the centralized in-memory coordinator managing real-time stopwatch state.
 *
 * This component acts as the 'Hot' Source of Truth, reconciling high-frequency transient
 * updates from execution logic (Ticker) with authoritative persistent state (Database).
 *
 * Implementations must ensure that state transitions are reactive and provide a
 * consistent snapshot for repository-level business logic.
 */
interface StopwatchInMemoryStateManager {

    /**
     * Reactive stream of the current [StopwatchModel].
     * primary entry point for Domain and UI layers to observe real-time updates.
     */
    val state: StateFlow<StopwatchModel>

    /**
     * Returns a synchronous snapshot of the current state.
     * Used for immediate validation or pre-persistence mapping.
     */
    fun getCurrentState(): StopwatchModel

    /**
     * Updates the in-memory state with authoritative data from the database.
     * Use this for initial synchronization, process death recovery, or
     * reconciling state after a write operation.
     *
     * @param dbModel The validated model from the local data source.
     */
    fun updateFromDatabase(dbModel: StopwatchModel)

    /**
     * Injects high-frequency snapshots into the state stream.
     * Optimized for performance; these updates are transient and bypass
     * immediate disk persistence to reduce I/O overhead.
     *
     * @param updatedStopwatch The real-time snapshot emitted by the ticker.
     */
    fun updateFromTicker(updatedStopwatch: StopwatchModel)

}