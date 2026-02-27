package com.example.smartalarm.feature.timer.data.startup

import com.example.smartalarm.core.application.startup.AppStartupTask
import com.example.smartalarm.feature.timer.data.sync.ShowTimerDbSyncManager
import javax.inject.Inject
import javax.inject.Singleton


@Suppress("UNUSED_PARAMETER")
@Singleton
class ShowTimerStartup @Inject constructor(
    private val dbSyncManager: ShowTimerDbSyncManager  // Injected to trigger initialization during app startup
) : AppStartupTask {

    /**
     * Starts the app startup task, which triggers initialization of [ShowTimerDbSyncManager].
     *
     * This method doesn't perform any direct operations but relies on the injected [dbSyncManager] to start its initialization
     * process when the [ShowTimerStartup] is instantiated.
     */
    override fun start() {
        // Just by constructing dbSyncManager, init starts
    }
}
