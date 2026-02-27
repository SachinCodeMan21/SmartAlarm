package com.example.smartalarm.feature.stopwatch.framework.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import com.example.smartalarm.feature.stopwatch.framework.notification.manager.StopwatchNotificationManager
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A foreground [android.app.Service] responsible for managing stopwatch operations in the background,
 * including displaying and updating notifications, saving state, and handling stopwatch events.
 *
 * This service is triggered via broadcast intents and uses Hilt for dependency injection.
 */

@AndroidEntryPoint
class StopwatchService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 104
    }

    @Inject lateinit var stopWatchUseCase: StopwatchUseCases

    @Inject lateinit var systemClockHelper: SystemClockHelper
    @Inject lateinit var stopWatchNotificationManager: StopwatchNotificationManager


    private var lastLapCount = 0
    private var isStopwatchReset = false
    private var lastStateWasRunning: Boolean? = null
    private var lastNotificationUpdateTime: Long = 0L


    private var tickerJob : Job? = null
    private var serviceScope: CoroutineScope? = null


    // ---------------------------------------------------------------------
    // Stopwatch Lifecycle Methods
    // ---------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        observeStopwatchState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            StopWatchBroadCastAction.START_FOREGROUND -> showStopWatchForegroundNotification()
            StopWatchBroadCastAction.PAUSE -> pauseStopWatchNotification()
            StopWatchBroadCastAction.RESUME -> resumeStopWatchNotification()
            StopWatchBroadCastAction.RESET -> resetStopWatchNotification()
            StopWatchBroadCastAction.LAP -> recordLapStopWatchNotification()
            StopWatchBroadCastAction.STOP_FOREGROUND -> stopStopWatchNotification()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Cancels the coroutine scope and its children
        serviceScope?.cancel()
    }


    // ---------------------------------------------------------------------
    // Stopwatch State Observer
    // ---------------------------------------------------------------------

    private fun observeStopwatchState() {

        serviceScope?.launch {

            stopWatchUseCase.getStopwatch().collect { state ->

                // Skip updates if stopwatch has been reset
                if (isStopwatchReset) return@collect

                // THRESHOLD LOGIC:
                val currentTime = systemClockHelper.getCurrentTime()
                val isStatusChanged = lastStateWasRunning != state.isRunning
                val lapCountChanged = lastLapCount != state.lapCount


                // Update immediately if:
                // 1. It's a Pause/Resume (isStatusChanged)
                // 2. It's a Lap
                // 3. Or if 1 second has passed
                if (isStatusChanged || lapCountChanged || currentTime - lastNotificationUpdateTime >= 1000L) {
                    if (state.elapsedTime>0){ updateForegroundNotification(state) }
                    lastNotificationUpdateTime = currentTime
                    lastStateWasRunning = state.isRunning
                    lastLapCount = state.lapCount
                }

                // Ticker management (Reactive)
                if (state.isRunning) startTicker() else stopTicker()
            }
        }
    }


    // ---------------------------------------------------------------------
    // Stopwatch Event Handlers
    // ---------------------------------------------------------------------

    // Promotes the service to foreground and starts notification updates if the stopwatch is currently running
    private fun showStopWatchForegroundNotification() = serviceScope?.launch {

        val currentStopwatch = stopWatchUseCase.getCurrentStopwatch()
        showForegroundNotification()

        if (currentStopwatch.isRunning) { startTicker() }

    }

    // Pauses the stopwatch, stops update jobs, and refreshes the notification state
    private fun pauseStopWatchNotification() = serviceScope?.launch {
        val result = stopWatchUseCase.pauseStopwatch()
        handleUseCaseError(result, "Pause Failed")
    }

    // Resumes the stopwatch, updates the notification, and restarts update jobs
    private fun resumeStopWatchNotification() = serviceScope?.launch {
        val result = stopWatchUseCase.startStopwatch()
        handleUseCaseError(result, "Resume Failed")
    }

    // Resets the stopwatch, removes the foreground notification, and stops the service lifecycle
    private fun resetStopWatchNotification() = serviceScope?.launch {
        val result = stopWatchUseCase.deleteStopwatch()
        isStopwatchReset = true
        handleUseCaseError(result, "Reset Database Failed")
        stopStopWatchNotification()
    }

    // Records a lap for the active stopwatch and updates state Stops the service if the operation fails
    private fun recordLapStopWatchNotification() = serviceScope?.launch {
        val result = stopWatchUseCase.lapStopwatch()
        handleUseCaseError(result, "Lap Failed")
    }

    // Cleans up all stopwatch-related work and terminates the foreground service
    private fun stopStopWatchNotification() {
        stopTicker()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }



    // ---------------------------------------------------------------------
    // Notification  Helper Methods
    // ---------------------------------------------------------------------

    // Builds and displays the initial foreground notification for the current stopwatch state
    private suspend fun showForegroundNotification() {
        val notificationModel = StopwatchNotificationModel.ActiveStopwatchModel(stopWatchUseCase.getCurrentStopwatch())
        val notification = stopWatchNotificationManager.getStopwatchNotification(notificationModel)
        withContext(Dispatchers.Main) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    // Updates the existing foreground notification with the latest stopwatch state
    private fun updateForegroundNotification(updatedStopwatch : StopwatchModel) {
        val notificationModel = StopwatchNotificationModel.ActiveStopwatchModel(updatedStopwatch)
        stopWatchNotificationManager.updateStopwatchNotification(NOTIFICATION_ID, notificationModel)
    }



    // Job Manager Methods
    private fun startTicker() {
        if (tickerJob != null) return
        tickerJob = serviceScope?.launch(Dispatchers.Default) {
            while (isActive) {
                stopWatchUseCase.updateStopwatchTicker()
                delay(100L)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    // ---------------------------------------------------------------------
    // Error Handling Helper
    // ---------------------------------------------------------------------

    private fun handleUseCaseError(result: MyResult<Unit, DataError>, logTag: String) {
        if (result is MyResult.Error) {

            val error = result.error

            // Option 1: Detailed Logging
            // Since Unexpected carries a throwable, we can log the actual stack trace
            when (error) {
                is DataError.Unexpected -> {
                    Log.e(logTag, "Unexpected system error", error.throwable)
                    // FirebaseCrashlytics.getInstance().recordException(error.throwable)
                }
                else -> Log.e(logTag, "$logTag Error: $error")
            }

            // Option 2: Safety Shutdown Logic
            // Check for specific database-critical states that require stopping the service
            val isCriticalLocalError = error is DataError.Local && (
                    error == DataError.Local.DISK_FULL || error == DataError.Local.CORRUPTED)

            if (isCriticalLocalError) {
                stopStopWatchNotification()
                // Optional: You might also want to toggle a "Service Error" state
                // in your repository so the UI knows the timer stopped due to a crash.
            }
        }
    }



}