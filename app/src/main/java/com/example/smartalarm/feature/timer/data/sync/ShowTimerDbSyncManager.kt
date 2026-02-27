package com.example.smartalarm.feature.timer.data.sync

import com.example.smartalarm.core.framework.di.annotations.ApplicationScope
import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.data.manager.TimerInMemoryStateManager
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper.toDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton class responsible for synchronizing the timer data between the local database and in-memory state.
 *
 * The `ShowTimerDbSyncManager` is responsible for listening to changes in the timer data stored in the local database and
 * updating the in-memory state manager accordingly. It ensures that the in-memory state is always consistent with the
 * data stored locally. The synchronization is done in a coroutine scope, leveraging Kotlin's Flow API for data collection.
 *
 * This class is injected into other parts of the application via the constructor and is used to ensure that the
 * application's in-memory timer state is updated when the local database changes, without blocking the main thread.
 *
 * @constructor Creates a new instance of `ShowTimerDbSyncManager` with the provided dependencies.
 * @param localDataSource The local data source responsible for fetching and updating timer data from the local database.
 * @param inMemoryStateManager The in-memory state manager responsible for holding the application's current timer state.
 * @param coroutineScope The coroutine scope in which the database collection process will run asynchronously.
 */
@Singleton
class ShowTimerDbSyncManager @Inject constructor(
    private val localDataSource: TimerLocalDataSource,
    private val inMemoryStateManager: TimerInMemoryStateManager,
    @param:ApplicationScope private val coroutineScope: CoroutineScope
) {

    init {
        coroutineScope.launch {
            // Collects timer data from the local database and updates the in-memory state.
            localDataSource.getTimerList().collect { entityList ->
                // Maps the database entities to domain models for further use.
                val domainModels = entityList.map { it.toDomainModel() }
                // Updates the in-memory state with the new data from the database.
                inMemoryStateManager.updateFromDatabase(domainModels)
            }
        }
    }

}

