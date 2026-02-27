package com.example.smartalarm.feature.timer.data.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.data.manager.TimerInMemoryStateManager
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper.toEntity
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Implementation of [TimerRepository] for managing timer data.
 */
class TimerRepositoryImpl @Inject constructor(
    private val localDataSource: TimerLocalDataSource,
    private val inMemoryStateManager: TimerInMemoryStateManager,
) : TimerRepository {

    /**
     * The current state of the timers.
     * Observes changes to the timer list in memory.
     */
    override val timerListState: StateFlow<List<TimerModel>> = inMemoryStateManager.state

    /**
     * Retrieves the current list of timers from memory.
     */
    override fun getCurrentTimerList(): List<TimerModel> = inMemoryStateManager.getCurrentTimers()

    /**
     * Updates the in-memory state via the Ticker (called by Service).
     */
    override fun tickAllRunningTimers() {
        inMemoryStateManager.updateFromTicker()
    }

    /**
     * Persists a timer. Maps SQLite/Room exceptions to [DataError.Local].
     */
    override suspend fun persistTimer(timerModel: TimerModel): MyResult<Unit, DataError> =
        myRunCatchingResult {
            val entity = timerModel.toEntity()
            localDataSource.saveTimer(entity)
        }

    /**
     * Deletes a timer. Maps potential IO or Database errors to [DataError.Local].
     */
    override suspend fun deleteTimerById(timerId: Int): MyResult<Unit, DataError> =
        myRunCatchingResult {
            localDataSource.deleteTimerById(timerId)
        }
}