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
 * Facilitates communication between the Repository layer and the [StopwatchDao],
 * translating high-level data requests into efficient database operations.
 *
 * @property dao The DAO responsible for interacting with the Room database.
 */
class StopwatchLocalDataSourceImpl @Inject constructor(
    private val dao: StopwatchDao
) : StopwatchLocalDataSource {

    /**
     * Observes the stopwatch session (state + laps) as a Flow.
     *
     * @param id The ID of the stopwatch session. Defaults to 1.
     * @return A [Flow] emitting the current [StopwatchWithLaps] or null if not initialized.
     */
    override fun observeStopwatchWithLaps(id: Int): Flow<StopwatchWithLaps?> {
        return dao.observeStopwatchWithLaps(id)
    }

    /**
     * Fetches a one-time snapshot of the stopwatch session.
     *
     * @param id The ID of the stopwatch session. Defaults to 1.
     * @return The [StopwatchWithLaps] snapshot if it exists, null otherwise.
     */
    override suspend fun getStopwatchWithLaps(id: Int): StopwatchWithLaps? {
        return dao.getStopwatchWithLaps(id)
    }

    /**
     * Persists the stopwatch state and its laps atomically.
     *
     * This ensures the state and laps remain consistent, avoiding stale or
     * duplicate lap entries.
     *
     * @param state The [StopwatchStateEntity] representing the current stopwatch state.
     * @param laps The list of [StopwatchLapEntity] records to persist.
     */
    override suspend fun saveStopwatchWithLaps(
        state: StopwatchStateEntity,
        laps: List<StopwatchLapEntity>
    ) {
        dao.syncStopwatchSession(state, laps)
    }

    /**
     * Deletes the stopwatch session and all associated laps.
     *
     * Due to the CASCADE foreign key in [StopwatchLapEntity], all laps
     * for this stopwatch are removed automatically.
     *
     * @param id The ID of the stopwatch session to delete. Defaults to 1.
     */
    override suspend fun deleteStopwatchSession(id: Int) {
        dao.deleteStopwatchState(id)
    }
}