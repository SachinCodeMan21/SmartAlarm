package com.example.smartalarm.feature.stopwatch.data.startup

import com.example.smartalarm.core.startup.AppStartupTask
import com.example.smartalarm.feature.stopwatch.data.sync.StopwatchDbSyncManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A startup task that triggers the initialization of the [StopwatchDbSyncManager] when the application starts.
 *
 * This class is used to initiate necessary database synchronization tasks during the app's startup.
 * It doesn't perform any direct work in the `start()` method but relies on the construction of the injected [StopwatchDbSyncManager]
 * to trigger its initialization process.
 */
@Suppress("UNUSED_PARAMETER")
@Singleton
class StopwatchStartup @Inject constructor(
    private val dbSyncManager: StopwatchDbSyncManager  // Injected to trigger initialization during app startup
) : AppStartupTask {

    /**
     * Starts the app startup task, which triggers initialization of [StopwatchDbSyncManager].
     *
     * This method doesn't perform any direct operations but relies on the injected [dbSyncManager] to start its initialization
     * process when the [StopwatchStartup] is instantiated.
     */
    override fun start() {
        // Just by constructing dbSyncManager, init starts
    }
}
