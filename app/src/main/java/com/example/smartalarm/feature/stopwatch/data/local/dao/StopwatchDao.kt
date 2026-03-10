package com.example.smartalarm.feature.stopwatch.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing stopwatch persistence.
 *
 * Provides database operations for both [StopwatchStateEntity] and
 * [StopwatchLapEntity]. This DAO acts as the primary entry point for
 * storing and retrieving stopwatch state and lap history.
 *
 * Transactional queries are used where necessary to ensure the
 * stopwatch state and its associated laps remain consistent.
 */
@Dao
interface StopwatchDao {

    // ====================================================
    // Basic Write Operations
    // ====================================================

    /** Inserts or updates the current stopwatch state. */
    @Upsert
    suspend fun upsertStopwatchState(state: StopwatchStateEntity)

    /** Inserts or updates a lap entry. */
    @Upsert
    suspend fun upsertLap(lap: StopwatchLapEntity)

    /**
     * Deletes the stopwatch state.
     *
     * Due to the CASCADE foreign key constraint defined in
     * [StopwatchLapEntity], all associated laps are automatically removed.
     */
    @Query("DELETE FROM stopwatch_state WHERE id = :id")
    suspend fun deleteStopwatchState(id: Int = 1)

    /** Deletes all laps associated with a specific stopwatch. */
    @Query("DELETE FROM stopwatch_laps WHERE stopwatch_id = :stopwatchId")
    suspend fun deleteLapsForStopwatch(stopwatchId: Int)



    // ====================================================
    // Read Operations (Observable / Transactional)
    // ====================================================

    /**
     * Observes the stopwatch state together with its laps.
     *
     * Wrapped in `@Transaction` to ensure the parent entity and its
     * related lap records are fetched consistently.
     */
    @Transaction
    @Query("SELECT * FROM stopwatch_state WHERE id = :id")
    fun observeStopwatchWithLaps(id: Int = 1): Flow<StopwatchWithLaps?>

    /**
     * Retrieves the stopwatch state and its laps once.
     */
    @Transaction
    @Query("SELECT * FROM stopwatch_state WHERE id = :id")
    suspend fun getStopwatchWithLaps(id: Int = 1): StopwatchWithLaps?



    // ====================================================
    // Transactional Session Operations
    // ====================================================

    /**
     * Synchronizes the entire stopwatch session.
     *
     * Persists the current stopwatch state and replaces all associated lap records
     * within a single transaction to maintain data consistency.
     *
     * Important:
     * - Existing laps are explicitly deleted before inserting the new list.
     *   This is necessary because `@Upsert` only inserts or updates individual laps;
     *   it does **not remove laps that were deleted or modified** in the UI.
     * - Example scenario: A user adds a new lap or deletes an old one. Without
     *   `deleteLapsForStopwatch(state.id)`, the old lap records would remain in
     *   the database, resulting in duplicate or stale entries.
     * - The CASCADE constraint in [StopwatchLapEntity] only applies if the
     *   parent [StopwatchStateEntity] itself is deleted.
     *
     * @param state The current stopwatch state to persist.
     * @param laps The full list of lap records to save. Existing laps for this
     *              stopwatch will be replaced by this list.
     */
    @Transaction
    suspend fun syncStopwatchSession(
        state: StopwatchStateEntity,
        laps: List<StopwatchLapEntity>
    ) {
        upsertStopwatchState(state)
        deleteLapsForStopwatch(state.id)
        laps.forEach { upsertLap(it) }
    }
}