package com.example.smartalarm.feature.stopwatch.data.sync

import com.example.smartalarm.core.framework.di.annotations.ApplicationScope
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper.toDomainModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Synchronization manager that bridges the gap between persistent storage and
 * the active in-memory stopwatch state.
 *
 * This class continuously observes the [StopwatchLocalDataSource] for any updates
 * to the stopwatch session. Whenever the database changes—whether from user actions,
 * background services, or system processes—it automatically maps the latest entities
 * into a [StopwatchModel] and updates the [StopwatchInMemoryStateManager].
 *
 * ### Key Responsibilities
 * 1. **Data Observation** – Maintains a reactive subscription to the database [Flow]
 *    for the primary stopwatch session.
 * 2. **Domain Mapping** – Converts Room entities into UI-agnostic domain models.
 * 3. **State Synchronization** – Ensures the in-memory source of truth remains
 *    consistent with the persistent SQLite source of truth.
 *
 * ### Lifecycle & Scope
 * This synchronization is launched within the [ApplicationScope], ensuring the
 * observation and synchronization pipeline remains active as long as the app
 * process is alive. Unlike a ViewModelScope, this prevents state desynchronization
 * when the user navigates away from the Stopwatch feature or the app goes to
 * the background.
 *
 * @property localDataSource The Room-backed data source providing stopwatch state and laps.
 * @property inMemoryStateManager Manages the live in-memory representation of the stopwatch.
 * @property scope CoroutineScope tied to the application lifecycle for continuous observation.
 */
@Singleton
class StopwatchDbSyncManager @Inject constructor(
    private val localDataSource: StopwatchLocalDataSource,
    private val inMemoryStateManager: StopwatchInMemoryStateManager,
    @param:ApplicationScope private val scope: CoroutineScope
) {

    /**
     * Initializes the synchronization pipeline.
     *
     * Observes the primary stopwatch session (default ID = 1) from the local data source.
     * If no session exists in the database, a fresh [StopwatchModel] is used.
     */
    init {
        scope.launch {
            localDataSource.observeStopwatchWithLaps()
                .collect { session ->
                    val domainModel = session?.toDomainModel() ?: StopwatchModel()
                    inMemoryStateManager.updateFromDatabase(domainModel)
                }
        }
    }
}