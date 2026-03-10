package com.example.smartalarm.feature.stopwatch.data.datasource.contract

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for accessing and modifying local stopwatch data.
 *
 * This layer abstracts the Room DAO to decouple database implementation from
 * Repository and Domain layers, enabling easier testing and maintenance.
 *
 * ### Singleton Stopwatch
 * All methods default to `id = 1` because the app currently supports a single
 * active stopwatch session. This ensures a single source of truth for both
 * state and laps. If multiple stopwatches are added in the future, these
 * defaults would need to be revisited.
 */
interface StopwatchLocalDataSource {

    /**
     * Provides a continuous stream of the stopwatch state and its laps.
     *
     * @param id The ID of the stopwatch instance. Defaults to 1.
     * @return A [Flow] emitting [StopwatchWithLaps] or null if the session is not initialized.
     */
    fun observeStopwatchWithLaps(id: Int = 1): Flow<StopwatchWithLaps?>

    /**
     * Fetches a one-time snapshot of the stopwatch session.
     *
     * @param id The ID of the stopwatch instance. Defaults to 1.
     * @return The [StopwatchWithLaps] snapshot if available, null otherwise.
     */
    suspend fun getStopwatchWithLaps(id: Int = 1): StopwatchWithLaps?

    /**
     * Persists the stopwatch state and its laps as an atomic unit.
     *
     * Typically used when the app is backgrounded or performing a full sync.
     *
     * @param state The current [StopwatchStateEntity].
     * @param laps The list of [StopwatchLapEntity] to persist.
     */
    suspend fun saveStopwatchWithLaps(state: StopwatchStateEntity, laps: List<StopwatchLapEntity>)

    /**
     * Deletes the stopwatch session and all associated laps.
     *
     * @param id The ID of the stopwatch session. Defaults to 1.
     */
    suspend fun deleteStopwatchSession(id: Int = 1)

}