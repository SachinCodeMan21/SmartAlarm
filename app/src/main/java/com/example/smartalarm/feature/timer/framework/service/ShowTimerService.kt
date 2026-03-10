package com.example.smartalarm.feature.timer.framework.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.analytics.ErrorLogger
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.service.handler.TimerNotificationHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.example.smartalarm.feature.timer.utility.TimerRingtonePlayer
import com.example.smartalarm.feature.timer.domain.model.TimerStatus
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.TimerUseCase
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerAnalyticsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import javax.inject.Inject


@AndroidEntryPoint
class ShowTimerService : Service() {

    companion object {
        const val ACTIVE_NOTIFICATION_ID = 1001
        const val COMPLETED_NOTIFICATION_ID = 1002
    }

    @Inject
    lateinit var timerUseCase: TimerUseCase

    @Inject
    lateinit var repository: TimerRepository

    @Inject
    lateinit var notificationHandler: TimerNotificationHandler

    @Inject
    lateinit var timerRingtoneHelper: TimerRingtonePlayer

    @Inject
    lateinit var errorLogger: ErrorLogger

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var tickerJob: Job? = null


    override fun onCreate() {
        super.onCreate()
        startObservingState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { i ->
            when (i.action) {

                TimerBroadCastAction.ACTION_START -> handleAction(
                    i,
                    action = { timerUseCase.startTimer(it) },
                    successAnalytics = { timer ->
                        analyticsHelper.logEvent(
                            ShowTimerAnalyticsEvent.SHOW_START_TIMER.eventName,
                            ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                            ShowTimerAnalyticsEvent.Params.TOTAL_ACTIVE_TIMERS to 1
                        )
                    },
                    errorTag = "StartTimer_ID_${i.getIntExtra(TimerKeys.TIMER_ID, -1)}"
                )

                TimerBroadCastAction.ACTION_PAUSE -> handleAction(
                    i,
                    action = { timerUseCase.pauseTimer(it) },
                    successAnalytics = { timer ->
                        analyticsHelper.logEvent(
                            ShowTimerAnalyticsEvent.SHOW_PAUSE_TIMER.eventName,
                            ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId
                        )
                    },
                    errorTag = "PauseTimer_ID_${i.getIntExtra(TimerKeys.TIMER_ID, -1)}"
                )

                TimerBroadCastAction.ACTION_RESUME -> handleAction(
                    i,
                    action = { timerUseCase.startTimer(it) },
                    successAnalytics = { timer ->
                        analyticsHelper.logEvent(
                            ShowTimerAnalyticsEvent.SHOW_RESUME_TIMER.eventName,
                            ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId
                        )
                    },
                    errorTag = "ResumeTimer_ID_${i.getIntExtra(TimerKeys.TIMER_ID, -1)}"
                )

                TimerBroadCastAction.ACTION_SNOOZE -> handleAction(
                    i,
                    action = { timerUseCase.snoozeTimer(it) },
                    successAnalytics = { timer ->
                        analyticsHelper.logEvent(
                            ShowTimerAnalyticsEvent.SHOW_SNOOZE_TIMER.eventName,
                            ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                            ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime
                        )
                    },
                    errorTag = "SnoozeTimer_ID_${i.getIntExtra(TimerKeys.TIMER_ID, -1)}"
                )

                TimerBroadCastAction.ACTION_STOP -> handleAction(
                    i,
                    action = { timerUseCase.restartTimer(it) },
                    successAnalytics = { timer ->
                        analyticsHelper.logEvent(
                            ShowTimerAnalyticsEvent.SHOW_RESTART_TIMER.eventName,
                            ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId
                        )
                    },
                    errorTag = "RestartTimer_ID_${i.getIntExtra(TimerKeys.TIMER_ID, -1)}"
                )

                TimerBroadCastAction.ACTION_TIMER_TIMEOUT ->
                    handleTimeoutAction(i)

                TimerBroadCastAction.ACTION_STOP_ALL_ACTIVE_TIMERS ->
                    stopAllActiveTimers()

                TimerBroadCastAction.ACTION_STOP_ALL_COMPLETED_TIMERS ->
                    stopAllCompletedTimers()

                TimerBroadCastAction.ACTION_STOP_FOREGROUND_TIMER ->
                    stopService()
            }
        }

        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        timerRingtoneHelper.stop()
    }

    // ────────────────────────────────────────────────────────────────
    //  1. The Reactive Core (Observer)
    // ────────────────────────────────────────────────────────────────

    private fun startObservingState() {
        serviceScope.launch {

            timerUseCase.getAllTimers().collect { timers ->

                // 1. Check if we should even exist
                if (timers.isEmpty()) {
                    stopService()
                    return@collect
                }

                // 2. Categorize Timers
                val runningTimers = timers.filter { it.isTimerRunning }
                val completedTimers =
                    timers.filter { it.remainingTime <= 0 && it.status != TimerStatus.STOPPED }
                val activeTimers =
                    timers.filter { it.remainingTime > 0 && it.status != TimerStatus.STOPPED }

                // 3. Manage the "Heartbeat" (Ticker)
                manageTicker(runningTimers.isNotEmpty())

                // 4. Update Notifications & Ringtone
                updateNotificationsAndRingtone(activeTimers, completedTimers)
            }
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  2. The Heartbeat (Ticker)
    // ────────────────────────────────────────────────────────────────

    private fun manageTicker(shouldRun: Boolean) {
        if (shouldRun) {
            if (tickerJob == null || tickerJob?.isActive == false) {
                tickerJob = serviceScope.launch(Dispatchers.Default) {
                    while (isActive) {
                        timerUseCase.tickTimerUseCase()
                        delay(1000L)
                    }
                }
            }
        } else {
            tickerJob?.cancel()
            tickerJob = null
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  3. The Renderer (Notifications & Audio)
    // ────────────────────────────────────────────────────────────────

    private fun updateNotificationsAndRingtone(
        activeTimers: List<TimerModel>,
        completedTimers: List<TimerModel>
    ) {


        // --- A. Ringtone Logic ---

        // Play if any timer is effectively "ringing" (completed but not stopped)
        val shouldRing = completedTimers.any { it.isTimerRunning }
        if (shouldRing) {
            timerRingtoneHelper.playDefaultTimer()
        } else {
            timerRingtoneHelper.stop()
        }

        // --- B. Notification Logic ---

        // Case 1: Active + Completed (Completed takes Foreground)
        if (activeTimers.isNotEmpty() && completedTimers.isNotEmpty()) {
            notificationHandler.showForegroundTimerNotification(
                this,
                COMPLETED_NOTIFICATION_ID,
                completedTimers
            )
            // Show active as a secondary normal notification
            notificationHandler.showNormalTimerNotification(activeTimers)
        }

        // Case 2: Only Completed
        else if (completedTimers.isNotEmpty()) {
            notificationHandler.showForegroundTimerNotification(
                this,
                COMPLETED_NOTIFICATION_ID,
                completedTimers
            )
            notificationHandler.removeNormalTimerNotification()
        }

        // Case 3: Only Active
        else if (activeTimers.isNotEmpty()) {
            notificationHandler.showForegroundTimerNotification(
                this,
                ACTIVE_NOTIFICATION_ID,
                activeTimers
            )
            notificationHandler.removeNormalTimerNotification() // Remove the secondary one
        }

        // Case 4: Nothing relevant (e.g. all reset)
        else {
            stopService()
        }
    }


    // ────────────────────────────────────────────────────────────────
    //  4. Action Handlers (Delegates to UseCase)
    // ────────────────────────────────────────────────────────────────

    private fun handleTimeoutAction(intent: Intent) {

        val timerId = intent.getIntExtra(TimerKeys.TIMER_ID, -1)
        if (timerId == -1) return // Invalid timer ID

        Log.d("TAG", "handleTimeoutAction executed")


        serviceScope.launch {

            val timer = getCurrentTimerList().find { it.timerId == timerId }
            timer?.let {
                val result = timerUseCase.restartTimer(timer)
                if (result is MyResult.Success) {
                    notificationHandler.showMissedTimerNotification(timer)
                } else {
                    logTimerError(result, "Timeout_Restart_ID_$timerId")
                }
            }
        }

    }

    private fun stopAllActiveTimers() {
        serviceScope.launch {
            val active = getCurrentTimerList().filter { it.remainingTime > 0 }
            active.forEach {
                val result = timerUseCase.restartTimer(it)
                if (result is MyResult.Error) {
                    logTimerError(result, "StopAllActive_ID_${it.timerId}")
                }

            }
        }
    }

    private fun stopAllCompletedTimers() {
        serviceScope.launch {
            val completed = getCurrentTimerList().filter { it.remainingTime <= 0 }
            completed.forEach {
                val result = timerUseCase.restartTimer(it)
                if (result is MyResult.Error) {
                    logTimerError(result, "StopAllCompleted_ID_${it.timerId}")
                }
            }
        }
    }

    private fun stopService() {
        tickerJob?.cancel()
        timerRingtoneHelper.stop()
        notificationHandler.removeNormalTimerNotification() // Clean up secondary
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    // ────────────────────────────────────────────────────────────────
    //  5. Helper Method
    // ────────────────────────────────────────────────────────────────

    private suspend fun getCurrentTimerList(): List<TimerModel> {
        return timerUseCase.getAllTimers().first()
    }

    private fun handleAction(
        intent: Intent,
        action: suspend (TimerModel) -> MyResult<Unit, DataError>,
        successAnalytics: ((TimerModel) -> Unit)? = null,
        errorTag: String? = null
    ) {
        val id = intent.getIntExtra(TimerKeys.TIMER_ID, -1)
        if (id == -1) return

        serviceScope.launch {
            val timer = getCurrentTimerList().find { it.timerId == id }
            timer?.let {
                try {
                    val result = action(it)
                    handleResult(result, it, successAnalytics, errorTag)
                } catch (e: Exception) {
                    errorLogger.recordException(e)
                }
            }
        }
    }

    private fun handleResult(
        result: MyResult<Unit, DataError>,
        timer: TimerModel,
        successAnalytics: ((TimerModel) -> Unit)? = null,
        errorTag: String? = null
    ) {
        when (result) {
            is MyResult.Success -> successAnalytics?.invoke(timer)
            is MyResult.Error -> {
                val throwable = if (result.error is DataError.Unexpected) {
                    result.error.throwable
                } else {
                    Exception("TimerService_Action_Error [${errorTag ?: "unknown"}]: ${result.error}")
                }
                errorLogger.recordException(throwable)
            }
        }
    }

    /**
     * Private helper to process UseCase results and report errors to Crashlytics.
     */
    private fun <T> logTimerError(result: MyResult<T, DataError>, actionTag: String) {
        if (result is MyResult.Error) {
            val error = result.error

            val throwable = if (error is DataError.Unexpected) {
                error.throwable
            } else {
                // Groups these as "TimerError" in the Firebase dashboard
                Exception("TimerError [$actionTag]: $error")
            }

            errorLogger.recordException(throwable)
        }
    }

}