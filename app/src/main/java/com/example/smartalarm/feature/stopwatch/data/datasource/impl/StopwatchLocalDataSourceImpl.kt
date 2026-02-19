package com.example.smartalarm.feature.stopwatch.data.datasource.impl

import com.example.smartalarm.feature.stopwatch.data.local.dao.StopWatchDao
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopWatchWithLaps
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [StopwatchLocalDataSource] that interacts with [StopWatchDao]
 * to perform local database operations related to stopwatches and their lap times.
 *
 * This class acts as a middle layer between the repository and the DAO,
 * abstracting Room-specific logic to maintain separation of concerns.
 *
 * @property dao DAO used to access stopwatch and lap data from the database.
 */
class StopwatchLocalDataSourceImpl @Inject constructor(
    private val dao: StopWatchDao
) : StopwatchLocalDataSource {



    /**
     * Observes the stopwatch and its laps for a given stopwatch ID.
     *
     * @param stopwatchId The ID of the stopwatch to observe.
     * @return A flow of [StopWatchWithLaps?], which emits the stopwatch and its laps, or `null` if not found.
     */
    override fun observeStopwatchWithLaps(stopwatchId: Int): Flow<StopWatchWithLaps?> {
        return dao.observeStopwatchWithLaps(stopwatchId)
    }


    /**
     * Retrieves a stopwatch and its laps by the given ID.
     *
     * @param stopwatchId The ID of the stopwatch to retrieve.
     * @return A [StopWatchWithLaps] object if found, or `null` otherwise.
     */
    override suspend fun getStopwatchWithLaps(stopwatchId: Int): StopWatchWithLaps? {
        return dao.getStopwatchWithLaps(stopwatchId)
    }


    /**
     * Saves or updates a stopwatch along with its associated lap records in the database.
     *
     * @param stopwatchEntity The [StopWatchEntity] to save or update.
     * @param laps A list of [StopWatchLapEntity] associated with the stopwatch.
     */
    override suspend fun saveStopwatchWithLaps(stopwatchEntity: StopWatchEntity, laps: List<StopWatchLapEntity>) {
        return dao.upsertStopwatchWithLaps(stopwatchEntity, laps)
    }


    /**
     * Deletes the stopwatch and all its associated lap records by ID.
     *
     * @param stopwatchId The ID of the stopwatch to delete.
     */
    override suspend fun deleteStopwatchWithLaps(stopwatchId: Int){
        return dao.deleteStopwatchWithLaps(stopwatchId)
    }


}
