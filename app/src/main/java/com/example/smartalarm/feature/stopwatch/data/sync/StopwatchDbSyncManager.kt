package com.example.smartalarm.feature.stopwatch.data.sync

import com.example.smartalarm.core.di.annotations.ApplicationScope
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper.toDomainModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A reactive synchronization manager responsible for bridging the gap between persistent storage
 * and active application state.
 *
 * This manager observes the [StopwatchLocalDataSource] for any changes to the stopwatch
 * session. Whenever the database is updated—whether by user action, a background service,
 * or system process—this class automatically maps the new database entities into a
 * [StopwatchModel] and pushes it to the [StopwatchInMemoryStateManager].
 *
 * ### Key Responsibilities:
 * 1. **Data Observation**: Maintains a long-running collection of the database [Flow].
 * 2. **Domain Mapping**: Converts database-specific entities into UI-agnostic domain models.
 * 3. **State Synchronization**: Ensures the in-memory "Source of Truth" is always consistent
 * with the physical SQLite "Source of Truth."
 *
 * ### Lifecycle & Scope:
 * This synchronization is launched within the [ApplicationScope]. Unlike a
 * ViewModelScope which dies when the user leaves a screen, this scope ensures
 * that database observation remains active as long as the app process is alive.
 * This prevents data loss or state desynchronization during background transitions.
 */
@Singleton
class StopwatchDbSyncManager @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
    @param:ApplicationScope private val scope: CoroutineScope
) {

    /**
     * Initializes the synchronization pipeline.
     * * Uses the default ID configured in the data source to observe the primary
     * stopwatch session. If the database is empty or a session is missing,
     * it defaults to a fresh [StopwatchModel].
     */
    init {
        scope.launch {
            // We no longer pass an ID here, as the DataStore defaults to ID 1.
            localDataSource.observeStopwatchWithLaps()
                .collect { session ->
                    val domainModel = session?.toDomainModel() ?: StopwatchModel()
                    inMemoryStateManager.updateFromDatabase(domainModel)
                }
        }
    }
}