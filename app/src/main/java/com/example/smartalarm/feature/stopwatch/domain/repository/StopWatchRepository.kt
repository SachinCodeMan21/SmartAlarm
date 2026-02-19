package com.example.smartalarm.feature.stopwatch.domain.repository

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing stopwatch data.
 */
interface StopWatchRepository {

    /**
     * The current observable state of the stopwatch.
     */
    val stopwatchState: StateFlow<StopwatchModel>

    /**
     * Retrieves the current stopwatch state.
     * @return The current [StopwatchModel].
     */
    fun getCurrentStopwatchState(): StopwatchModel

    /**
     * Updates the stopwatch state with real-time data from the ticker.
     * @param updatedStopwatch The updated [StopwatchModel] from the ticker.
     */
    fun updateTickerState(updatedStopwatch: StopwatchModel)

    suspend fun getBootStopwatchState(stopwatchId: Int): StopwatchModel?

    /**
     * Persists the current stopwatch state to the local data source.
     * @param stopwatchModel The [StopwatchModel] to persist.
     * @return A [Result] indicating success or failure.
     */
    suspend fun persistStopwatch(stopwatchModel: StopwatchModel): Result<Unit>

    /**
     * Deletes the stopwatch with the given ID from the local data source.
     * @param stopwatchId The ID of the stopwatch to delete.
     * @return A [Result] indicating success or failure.
     */
    suspend fun deleteStopwatch(stopwatchId: Int): Result<Unit>
}