package com.example.smartalarm.feature.stopwatch.data.datasource.contract

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopWatchWithLaps
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for accessing and modifying local stopwatch-related data.
 * Abstracts the Room DAO layer to decouple database operations from higher-level components.
 */
interface StopwatchLocalDataSource {

    /**
     * Observes the state of a stopwatch and its laps.
     *
     * Combines two flows: one for the stopwatch state and one for the laps.
     * Returns a flow of [StopWatchWithLaps?], emitting `null` if no stopwatch is found.
     *
     * @param stopwatchId The ID of the stopwatch to observe.
     * @return A flow of [StopWatchWithLaps?].
     */
    fun observeStopwatchWithLaps(stopwatchId: Int): Flow<StopWatchWithLaps?>

    /**
     * Retrieves a stopwatch and its lap times by ID from the local database.
     *
     * @param stopwatchId The ID of the stopwatch.
     * @return A [StopWatchWithLaps] if found, or `null` if no matching stopwatch exists.
     */
    suspend fun getStopwatchWithLaps(stopwatchId: Int): StopWatchWithLaps?

    /**
     * Saves a stopwatch and its associated lap times in a single transaction.
     * Existing entries with matching IDs will be updated.
     *
     * The provided laps must belong to the given [stopwatchEntity].
     *
     * @param stopwatchEntity The [StopWatchEntity] to save or update.
     * @param laps A list of [StopWatchLapEntity] associated with the stopwatch.
     */
    suspend fun saveStopwatchWithLaps(stopwatchEntity: StopWatchEntity, laps: List<StopWatchLapEntity>)


    /**
     * Deletes a stopwatch and its associated laps by ID from the local database.
     *
     * @param stopwatchId The ID of the stopwatch to delete.
     */
    suspend fun deleteStopwatchWithLaps(stopwatchId: Int)

}
