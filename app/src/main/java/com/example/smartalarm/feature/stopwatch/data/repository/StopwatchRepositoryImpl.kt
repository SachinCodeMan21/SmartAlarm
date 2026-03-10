package com.example.smartalarm.feature.stopwatch.data.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper.toEntity
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Production-ready implementation of [StopwatchRepository].
 *
 * Coordinates data flow between [StopwatchLocalDataSource] (Persistence)
 * and [StopwatchInMemoryStateManager] (Hot State). It implements an
 * 'In-Memory-First' strategy for high-frequency updates while maintaining
 * database consistency for critical session milestones.
 */
class StopwatchRepositoryImpl @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
) : StopwatchRepository {

    override val stopwatchState: StateFlow<StopwatchModel> = inMemoryStateManager.state

    override fun getCurrentStopwatchState(): StopwatchModel =
        inMemoryStateManager.getCurrentState()

    /**
     * Dispatches transient updates to the hot state manager.
     * Persistence is intentionally bypassed here to protect the device
     * from excessive SQLite write cycles during millisecond-level ticking.
     */
    override fun updateTickerState(updatedStopwatch: StopwatchModel) =
        inMemoryStateManager.updateFromTicker(updatedStopwatch)

    /**
     * Executes atomic persistence of the session.
     * Transforms the Domain [StopwatchModel] into Data Entities via [StopwatchMapper]
     * before delegating to the local data source.
     */
    override suspend fun persistStopwatch(stopwatchModel: StopwatchModel): MyResult<Unit, DataError> =
        myRunCatchingResult {
            val stateEntity = stopwatchModel.toEntity()
            val lapEntities = stopwatchModel.lapTimes.map { it.toEntity(stateEntity.id) }
            localDataSource.saveStopwatchWithLaps(stateEntity, lapEntities)
        }

    override suspend fun deleteStopwatch(): MyResult<Unit, DataError> =
        myRunCatchingResult {
            localDataSource.deleteStopwatchSession()
        }
}