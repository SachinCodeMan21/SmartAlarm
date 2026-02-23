package com.example.smartalarm.feature.stopwatch.data.datasource.impl

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopwatchDao
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Room-backed implementation of [StopwatchLocalDataSource].
 *
 * This class facilitates communication between the Repository layer and the
 * [StopwatchDao], ensuring that high-level data requests are translated into
 * efficient database operations.
 *
 * @property dao The Data Access Object responsible for SQLite interactions.
 */
class StopwatchLocalDataSourceImpl @Inject constructor(
    private val dao: StopwatchDao
) : StopwatchLocalDataSource {

    /**
     * Streams the stopwatch session (state + laps) from the database.
     * * @param id The ID of the stopwatch session (Defaults to 1).
     */
    override fun observeStopwatchWithLaps(id: Int): Flow<StopwatchWithLaps?> {
        return dao.observeStopwatchWithLaps(id)
    }

    /**
     * Fetches a single snapshot of the stopwatch session.
     * * @param id The ID of the stopwatch session (Defaults to 1).
     */
    override suspend fun getStopwatchWithLaps(id: Int): StopwatchWithLaps? {
        return dao.getStopwatchWithLaps(id)
    }

    /**
     * Persists the stopwatch state and its laps atomically using a Room transaction.
     *
     * @param state The [StopwatchStateEntity] representing the current timer progress.
     * @param laps The collection of [StopwatchLapEntity] records to persist.
     */
    override suspend fun saveStopwatchWithLaps(
        state: StopwatchStateEntity,
        laps: List<StopwatchLapEntity>
    ) {
        dao.syncStopwatchSession(state, laps)
    }

    /**
     * Removes the stopwatch session.
     * Due to the cascade constraint, this also wipes all associated laps.
     * * @param id The ID of the stopwatch session to delete.
     */
    override suspend fun deleteStopwatchSession(id: Int) {
        dao.deleteStopwatchState(id)
    }
}
