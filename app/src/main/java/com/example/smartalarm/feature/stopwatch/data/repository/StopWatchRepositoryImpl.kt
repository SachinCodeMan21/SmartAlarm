package com.example.smartalarm.feature.stopwatch.data.repository

import com.example.smartalarm.core.utility.extension.runCatchingResult
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopWatchMapper.toDomainModel
import com.example.smartalarm.feature.stopwatch.data.mapper.StopWatchMapper.toEntity
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Implementation of [StopWatchRepository] for managing stopwatch data.
 */
class StopWatchRepositoryImpl @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
) : StopWatchRepository {

    /**
     * The current state of the stopwatch.
     * Observes changes to the stopwatch state in memory.
     */
    override val stopwatchState: StateFlow<StopwatchModel> = inMemoryStateManager.state

    /**
     * Retrieves the current stopwatch state.
     * @return The current [StopwatchModel] from in-memory state.
     */
    override fun getCurrentStopwatchState(): StopwatchModel = inMemoryStateManager.getCurrentState()

    /**
     * Updates the stopwatch state with real-time data from the ticker.
     * @param updatedStopwatch The updated [StopwatchModel] from the ticker.
     */
    override fun updateTickerState(updatedStopwatch: StopwatchModel) = inMemoryStateManager.updateFromTicker(updatedStopwatch)


    override suspend fun getBootStopwatchState(stopwatchId: Int): StopwatchModel?{
        return localDataSource.getStopwatchWithLaps(stopwatchId)?.let { (stopwatchEntity,lapEntity) ->
            stopwatchEntity.toDomainModel(lapEntity)
        }
    }


    /**
     * Persists the stopwatch state and its laps to the local data source.
     * @param stopwatchModel The [StopwatchModel] to persist.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun persistStopwatch(stopwatchModel: StopwatchModel): Result<Unit> =
        runCatchingResult {
            val entity = stopwatchModel.toEntity()
            val laps = stopwatchModel.lapTimes.map { it.toEntity(entity.id) }
            localDataSource.saveStopwatchWithLaps(entity, laps)
        }

    /**
     * Deletes the stopwatch from the local data source.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun deleteStopwatch(stopwatchId: Int): Result<Unit> =
        runCatchingResult {
            localDataSource.deleteStopwatchWithLaps(stopwatchId)
        }

}