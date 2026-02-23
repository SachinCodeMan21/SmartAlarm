package com.example.smartalarm.feature.stopwatch.data.datasource.contract

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for accessing and modifying local stopwatch-related data.
 *
 * This layer abstracts the Room DAO to decouple the database implementation from
 * higher-level Repository and Domain layers, facilitating easier testing and
 * maintenance.
 */
interface StopwatchLocalDataSource {

    /**
     * Provides a continuous stream of the stopwatch state and its associated laps.
     * * @param id The unique identifier of the stopwatch instance (Default is 1).
     * @return A [Flow] emitting the combined state and lap data, or null if not initialized.
     */
    fun observeStopwatchWithLaps(id: Int = 1): Flow<StopwatchWithLaps?>

    /**
     * Performs a one-time fetch of the current stopwatch session.
     *
     * @param id The unique identifier of the stopwatch instance (Default is 1).
     * @return The [StopwatchWithLaps] snapshot if it exists.
     */
    suspend fun getStopwatchWithLaps(id: Int = 1): StopwatchWithLaps?

    /**
     * Persists the complete stopwatch session—both state and laps—as an atomic unit.
     * This is typically used for full synchronization or when the app is backgrounded.
     *
     * @param state The current [StopwatchStateEntity].
     * @param laps The list of [StopwatchLapEntity] associated with this state.
     */
    suspend fun saveStopwatchWithLaps(state: StopwatchStateEntity, laps: List<StopwatchLapEntity>)

    /**
     * Deletes the stopwatch session from the local database.
     * * Note: Due to foreign key constraints, this operation automatically
     * purges all associated lap records (CASCADE delete).
     *
     * @param id The ID of the stopwatch session to remove.
     */
    suspend fun deleteStopwatchSession(id: Int = 1)
}
