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
 * Data Access Object (DAO) for managing [StopwatchStateEntity] and [StopwatchLapEntity].
 *
 * This interface serves as the primary entry point for stopwatch persistence logic.
 * It leverages SQLite transactions to maintain referential integrity between the
 * global stopwatch state and its collection of laps.
 */
@Dao
interface StopwatchDao {

    // ====================================================
    // Simple Operations
    // ====================================================

    @Upsert
    suspend fun upsertStopwatchState(state: StopwatchStateEntity)

    @Upsert
    suspend fun upsertLap(lap: StopwatchLapEntity)

    /**
     * Deletes the stopwatch state. Due to the ForeignKey CASCADE defined in
     * [StopwatchLapEntity], this will automatically delete all associated laps.
     */
    @Query("DELETE FROM stopwatch_state WHERE id = :id")
    suspend fun deleteStopwatchState(id: Int = 1)

    @Query("DELETE FROM stopwatch_laps WHERE stopwatch_id = :stopwatchId")
    suspend fun deleteLapsForStopwatch(stopwatchId: Int)



    // ====================================================
    // Observable & Transactional Queries
    // ====================================================

    /**
     * Provides a real-time stream of the stopwatch state and its laps.
     * Wrapped in @Transaction to ensure the list of laps stays in sync with
     * the state's metadata during the multi-table fetch.
     */
    @Transaction
    @Query("SELECT * FROM stopwatch_state WHERE id = :id")
    fun observeStopwatchWithLaps(id: Int = 1): Flow<StopwatchWithLaps?>

    /**
     * One-shot fetch of the stopwatch and its laps.
     */
    @Transaction
    @Query("SELECT * FROM stopwatch_state WHERE id = :id")
    suspend fun getStopwatchWithLaps(id: Int = 1): StopwatchWithLaps?




    // ====================================================
    // Atomic Business Logic
    // ====================================================

    /**
     * Synchronizes the entire stopwatch session to the database.
     * Often used when the app is being backgrounded or closed.
     */
    @Transaction
    suspend fun syncStopwatchSession(state: StopwatchStateEntity, laps: List<StopwatchLapEntity>) {
        upsertStopwatchState(state)
        deleteLapsForStopwatch(state.id)
        laps.forEach { upsertLap(it) }
    }
}






















//
///**
// * Data Access Object (DAO) for accessing stopwatch and lap-related data.
// *
// * This interface defines methods for querying, inserting, updating, and deleting
// * stopwatch records along with their associated lap times. It ensures proper
// * transactional handling when updating both stopwatch and lap entities together.
// */
//@Dao
//interface StopWatchDao {
//
//    // ====================================================
//    // Non-Transaction Queries
//    // ====================================================
//
//    /**
//     * Inserts or updates a [StopwatchStateEntity] in the database.
//     *
//     * If a stopwatch with the same ID exists, it will be updated.
//     */
//    @Upsert
//    suspend fun upsertStopwatch(stopWatchEntity: StopwatchStateEntity)
//
//    /**
//     * Inserts or updates a [StopwatchLapEntity] in the database.
//     *
//     * If a lap with the same primary key exists, it will be updated.
//     */
//    @Upsert
//    suspend fun upsertStopwatchLap(stopWatchLapEntity: StopwatchLapEntity)
//
//    /**
//     * Deletes a stopwatch by its ID.
//     *
//     * @param stopwatchId The ID of the stopwatch to delete.
//     */
//    @Query("DELETE FROM stopwatch_table WHERE id = :stopwatchId")
//    suspend fun deleteStopwatchWithLaps(stopwatchId: Int)
//
//
//
//    // ====================================================
//    // Transaction Queries
//    // ====================================================
//
//    @Query("SELECT * FROM stopwatch_table WHERE id = :stopwatchId")
//    fun observeStopwatchWithLaps(stopwatchId: Int): Flow<StopWatchWithLaps?>
//
//    /**
//     * Retrieves a stopwatch along with its associated lap records by ID.
//     *
//     * This method runs in a transaction to ensure that the stopwatch
//     * and its laps are fetched atomically.
//     *
//     * @param stopwatchId The ID of the stopwatch.
//     * @return A [StopWatchWithLaps] object if found, otherwise null.
//     */
//    @Transaction
//    @Query("SELECT * FROM stopwatch_table WHERE id = :stopwatchId")
//    suspend fun getStopwatchWithLaps(stopwatchId: Int): StopWatchWithLaps?
//
//    /**
//     * Inserts or updates a stopwatch and its lap records in a single transaction.
//     *
//     * Ensures that both the stopwatch and its laps are written atomically.
//     *
//     * @param stopWatchEntity The [StopwatchStateEntity] to insert or update.
//     * @param laps A list of [StopwatchLapEntity] to insert or update.
//     */
//    @Transaction
//    suspend fun upsertStopwatchWithLaps(stopWatchEntity: StopwatchStateEntity, laps: List<StopwatchLapEntity>) {
//        upsertStopwatch(stopWatchEntity)
//        laps.forEach { upsertStopwatchLap(it) }
//    }
//
//}
