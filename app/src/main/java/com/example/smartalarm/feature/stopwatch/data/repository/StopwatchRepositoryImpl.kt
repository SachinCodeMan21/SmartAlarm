package com.example.smartalarm.feature.stopwatch.data.repository

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.GeneralErrorMapper
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper.toEntity
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Implementation of [StopwatchRepository] that coordinates data flow between
 * the persistent [StopwatchLocalDataSource] and the live [StopwatchInMemoryStateManager].
 *
 * ### Why this exists:
 * The repository acts as the "Traffic Controller." It decides when to update the
 * high-frequency in-memory state (via the ticker) and when to perform the
 * heavy-lifting of disk persistence (via Room).
 */
class StopwatchRepositoryImpl @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
) : StopwatchRepository {

    /**
     * Reactive stream of the current stopwatch session.
     * ViewModels should observe this as their primary source of truth.
     */
    override val stopwatchState: StateFlow<StopwatchModel> = inMemoryStateManager.state

    /**
     * Synchronous access to the current in-memory snapshot.
     */
    override fun getCurrentStopwatchState(): StopwatchModel =
        inMemoryStateManager.getCurrentState()

    /**
     * Routes high-frequency ticker updates to memory for UI smoothness.
     * Note: This does NOT save to the database to avoid excessive disk I/O.
     */
    override fun updateTickerState(updatedStopwatch: StopwatchModel) =
        inMemoryStateManager.updateFromTicker(updatedStopwatch)


    /**
     * Persists the entire stopwatch session to the local database.
     * * ### Process:
     * 1. Converts the pure domain model into database-ready entities.
     * 2. Uses the Mapper to re-attach the singleton ID (1).
     * 3. Performs an atomic transaction to save both state and laps.
     * 4. Uses [GeneralErrorMapper.mapDatabaseException] to convert SQLite/Room
     *    failures into domain-specific [DataError]s.
     */
    override suspend fun persistStopwatch(stopwatchModel: StopwatchModel): MyResult<Unit, DataError> {
        return try {
            val stateEntity = stopwatchModel.toEntity()
            val lapEntities = stopwatchModel.lapTimes.map { it.toEntity(stateEntity.id) }
            localDataSource.saveStopwatchWithLaps(stateEntity, lapEntities)
            MyResult.Success(Unit)
        } catch (e: Exception) {
            MyResult.Error(GeneralErrorMapper.mapDatabaseException(e))
        }
    }

    /**
     * Purges the stopwatch session from the database.
     * Returns a [MyResult.Error] with [DataError.Local] details if the operation fails.
     */
    override suspend fun deleteStopwatch(): MyResult<Unit, DataError> {
        return try {
            localDataSource.deleteStopwatchSession()
            MyResult.Success(Unit)
        } catch (e: Throwable) {
            // Consistent error mapping for database operations
            MyResult.Error(GeneralErrorMapper.mapDatabaseException(e))
        }
    }

}
