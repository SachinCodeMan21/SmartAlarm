package com.example.smartalarm.feature.timer.data.repository

import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.extension.runCatchingResult
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
     * Persists a timer to the local data source.
     * Note: The UI updates via the 'init' block observer, not directly from here.
     */
    override suspend fun persistTimer(timerModel: TimerModel): Result<Unit> = runCatchingResult {
        val entity = timerModel.toEntity()
        localDataSource.saveTimer(entity)
    }

    /**
     * Deletes a timer from the local data source.
     */
    override suspend fun deleteTimerById(timerId: Int): Result<Unit> = runCatchingResult {
        localDataSource.deleteTimerById(timerId)
    }
}
//
///**
// * Implementation of the [TimerRepository] interface that provides a layer of abstraction over
// * the local data source for managing timer data.
// *
// * This class handles the conversion between [TimerEntity] and [TimerModel], ensuring that the
// * domain layer works only with models, while the data layer manages persistence.
// *
// * @property localDataSource A local data source responsible for actual storage and retrieval
// * of timer entities.
// */
//class TimerRepositoryImpl @Inject constructor(
//    private val localDataSource: TimerLocalDataSource
//) : TimerRepository {
//
//    /**
//     * Retrieves a flow of a list of timers from the local data source.
//     * Each [TimerEntity] is mapped to a [TimerModel].
//     *
//     * @return [Flow] emitting lists of [TimerModel]s.
//     */
//    override fun getTimerList(): Flow<List<TimerModel>> {
//        return localDataSource.getTimerList().map { entityList ->
//            entityList.map { it.toDomainModel() }
//        }
//    }
//
//    /**
//     * Saves a timer to the local data source after converting it to an entity.
//     *
//     * @param timerModel The [TimerModel] to be saved.
//     * @return [Result.Success] if saving was successful, or [Result.Error] with the exception.
//     */
//    override suspend fun saveTimer(timerModel: TimerModel): Result<Unit> = runCatchingResult {
//        val entity = timerModel.toEntity()
//        localDataSource.saveTimer(entity)
//    }
//
//    /**
//     * Deletes a timer identified by its ID from the local data source.
//     *
//     * @param timerId The unique ID of the timer to delete.
//     * @return [Result.Success] if deletion was successful, or [Result.Error] with the exception.
//     */
//    override suspend fun deleteTimerById(timerId: Int): Result<Unit> = runCatchingResult {
//        localDataSource.deleteTimerById(timerId)
//    }
//
//}
