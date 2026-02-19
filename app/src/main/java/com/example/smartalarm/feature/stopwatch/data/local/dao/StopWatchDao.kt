package com.example.smartalarm.feature.stopwatch.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopWatchWithLaps
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for accessing stopwatch and lap-related data.
 *
 * This interface defines methods for querying, inserting, updating, and deleting
 * stopwatch records along with their associated lap times. It ensures proper
 * transactional handling when updating both stopwatch and lap entities together.
 */
@Dao
interface StopWatchDao {

    // ====================================================
    // Non-Transaction Queries
    // ====================================================

    /**
     * Inserts or updates a [StopWatchEntity] in the database.
     *
     * If a stopwatch with the same ID exists, it will be updated.
     */
    @Upsert
    suspend fun upsertStopwatch(stopWatchEntity: StopWatchEntity)

    /**
     * Inserts or updates a [StopWatchLapEntity] in the database.
     *
     * If a lap with the same primary key exists, it will be updated.
     */
    @Upsert
    suspend fun upsertStopwatchLap(stopWatchLapEntity: StopWatchLapEntity)

    /**
     * Deletes a stopwatch by its ID.
     *
     * @param stopwatchId The ID of the stopwatch to delete.
     */
    @Query("DELETE FROM stopwatch_table WHERE id = :stopwatchId")
    suspend fun deleteStopwatchWithLaps(stopwatchId: Int)



    // ====================================================
    // Transaction Queries
    // ====================================================

    @Query("SELECT * FROM stopwatch_table WHERE id = :stopwatchId")
    fun observeStopwatchWithLaps(stopwatchId: Int): Flow<StopWatchWithLaps?>

    /**
     * Retrieves a stopwatch along with its associated lap records by ID.
     *
     * This method runs in a transaction to ensure that the stopwatch
     * and its laps are fetched atomically.
     *
     * @param stopwatchId The ID of the stopwatch.
     * @return A [StopWatchWithLaps] object if found, otherwise null.
     */
    @Transaction
    @Query("SELECT * FROM stopwatch_table WHERE id = :stopwatchId")
    suspend fun getStopwatchWithLaps(stopwatchId: Int): StopWatchWithLaps?

    /**
     * Inserts or updates a stopwatch and its lap records in a single transaction.
     *
     * Ensures that both the stopwatch and its laps are written atomically.
     *
     * @param stopWatchEntity The [StopWatchEntity] to insert or update.
     * @param laps A list of [StopWatchLapEntity] to insert or update.
     */
    @Transaction
    suspend fun upsertStopwatchWithLaps(stopWatchEntity: StopWatchEntity, laps: List<StopWatchLapEntity>) {
        upsertStopwatch(stopWatchEntity)
        laps.forEach { upsertStopwatchLap(it) }
    }

}
