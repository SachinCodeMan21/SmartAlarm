package com.example.smartalarm.feature.stopwatch.data.sync

import com.example.smartalarm.core.di.annotations.ApplicationScope
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopWatchMapper.toDomainModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages synchronization between the local data source and the in-memory state of the stopwatch.
 *
 * This class listens for changes to the stopwatch data and updates the in-memory state whenever
 * new data is observed in the local database. It is responsible for keeping the in-memory state
 * and database synchronized.
 */
@Singleton
class StopwatchDbSyncManager @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
    @param:ApplicationScope private val coroutineScope: CoroutineScope
) {
    companion object { private const val STOPWATCH_ID = 1 }

    /**
     * Initializes the [StopwatchDbSyncManager] and starts observing the stopwatch data in the local database.
     *
     * On initialization, this coroutine starts observing the stopwatch and its laps. When new data is
     * observed, the [StopwatchModel] is updated in the [inMemoryStateManager].
     */
    init {
        coroutineScope.launch {
            localDataSource.observeStopwatchWithLaps(STOPWATCH_ID)
                .collect { entity ->
                    val model = entity?.stopwatch?.toDomainModel(entity.laps)
                        ?: StopwatchModel()
                    inMemoryStateManager.updateFromDatabase(model)
                }
        }
    }

}
