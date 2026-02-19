package com.example.smartalarm.feature.timer.framework.jobmanager.contract

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import kotlinx.coroutines.CoroutineScope
/**
 * Interface for managing timer-related background update jobs in a service.
 *
 * This is used to periodically update active and completed timers,
 * and to notify the service or UI when timers are updated or completed.
 */
interface TimerServiceJobManager {

    /**
     * Starts a coroutine job that updates active timers every second.
     *
     * @param scope CoroutineScope in which the job will run (typically the service scope).
     * @param updateActiveTimers Function that updates and returns the current list of active timers.
     * @param hasRunningActiveTimers Function that determines if there are any running active timers.
     * @param onTimerUpdated Callback invoked when a timer is updated.
     * @param onTimerCompleted Callback invoked when all active timers are completed.
     */
//    fun startActiveTimerUpdates(
//        scope: CoroutineScope,
//        hasRunningActiveTimers: () -> Boolean,
//        onTimerUpdated: () -> Unit,
//        onTimerCompleted: () -> Unit
//    )

    fun startActiveTimerUpdates(
        scope: CoroutineScope,
        updateActiveTimers: () -> List<TimerModel>,
        hasRunningActiveTimers: () -> Boolean,
        onTimerUpdated: (TimerModel) -> Unit,
        onTimerCompleted: () -> Unit
    )



    /**
     * Starts a coroutine job that updates completed timers every second.
     *
     * @param scope CoroutineScope in which the job will run (typically the service scope).
     * @param updateCompletedTimers Function that updates and returns the current list of completed timers.
     * @param hasRunningCompletedTimers Function that determines if there are any running completed timers.
     * @param onTimerUpdated Callback invoked when a completed timer is updated.
     * @param onTimerCompleted Callback invoked when all completed timers are fully completed or no longer need updates.
     */
    fun startCompletedTimerUpdates(
        scope: CoroutineScope,
        updateCompletedTimers: () -> List<TimerModel>,
        hasRunningCompletedTimers: () -> Boolean,
        onTimerUpdated: (TimerModel) -> Unit,
        onTimerCompleted: () -> Unit
    )

/*    fun startCompletedTimerUpdates(
        scope: CoroutineScope,
        hasRunningCompletedTimers: () -> Boolean,
        onTimerUpdated: () -> Unit,
        onTimerCompleted: () -> Unit
    )*/

    /**
     * Stops the active timer update job if it's running.
     */
    fun stopActiveTimerUpdates()

    /**
     * Stops the completed timer update job if it's running.
     */
    fun stopCompletedTimerUpdates()

    /**
     * Stops all running update jobs (active and completed).
     */
    fun stopAllJobs()
}

