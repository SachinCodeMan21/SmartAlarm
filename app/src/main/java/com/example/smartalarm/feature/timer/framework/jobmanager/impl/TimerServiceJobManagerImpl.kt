package com.example.smartalarm.feature.timer.framework.jobmanager.impl

import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.jobmanager.contract.TimerServiceJobManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Implementation of [TimerServiceJobManager] responsible for managing periodic updates
 * to active and completed timers using background coroutines.
 *
 * @param defaultDispatcher Dispatcher on which timer update jobs will be executed.
 */
class TimerServiceJobManagerImpl @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : TimerServiceJobManager
{

    private var activeJob: Job? = null
    private var completedJob: Job? = null

    /**
     * Starts a background job to update active timers every second.
     *
     * @param scope Coroutine scope in which the job will run.
     * @param updateActiveTimers Function to update active timers and return the new list.
     * @param hasRunningActiveTimers Function to check if any active timer is still running.
     * @param onTimerUpdated Callback invoked when an active timer is updated.
     * @param onTimerCompleted Callback invoked when no more active timers are left.
     */
//    override fun startActiveTimerUpdates(
//        scope: CoroutineScope,
//        hasRunningActiveTimers: () -> Boolean,
//        onTimerUpdated: () -> Unit,
//        onTimerCompleted: () -> Unit
//    ) {
//        if (activeJob != null) return
//
//        activeJob = scope.launch(defaultDispatcher) {
//
//            while (isActive) {
//
//                delay(1_000L)
//
//                if (hasRunningActiveTimers()) {
//                    onTimerUpdated()
//                } else {
//                    onTimerCompleted()
//                    stopActiveTimerUpdates()
//                }
//            }
//        }
//    }

    override fun startActiveTimerUpdates(
        scope: CoroutineScope,
        updateActiveTimers: () -> List<TimerModel>,
        hasRunningActiveTimers: () -> Boolean,
        onTimerUpdated: (TimerModel) -> Unit,
        onTimerCompleted: () -> Unit
    ) {
        if (activeJob != null) return

        activeJob = scope.launch(defaultDispatcher) {

            while (isActive) {

                delay(1_000L)

                if (hasRunningActiveTimers()) {

                    val updatedActiveTimers = updateActiveTimers()

                    if (updatedActiveTimers.isEmpty()) {
                        stopActiveTimerUpdates()
                        onTimerCompleted()
                    }
                    else {
                        onTimerUpdated(updatedActiveTimers.first())
                    }
                } else {
                    stopActiveTimerUpdates()
                }
            }
        }
    }

    /**
     * Starts a background job to update completed timers every second.
     *
     * @param scope Coroutine scope in which the job will run.
     * @param updateCompletedTimers Function to update completed timers and return the new list.
     * @param hasRunningCompletedTimers Function to check if any completed timer is still running.
     * @param onTimerUpdated Callback invoked when a completed timer is updated.
     * @param onTimerCompleted Callback invoked when no more completed timers are left.
     */

    override fun startCompletedTimerUpdates(
        scope: CoroutineScope,
        updateCompletedTimers: () -> List<TimerModel>,
        hasRunningCompletedTimers: () -> Boolean,
        onTimerUpdated: (TimerModel) -> Unit,
        onTimerCompleted: () -> Unit
    ) {
        if (completedJob != null) return

        completedJob = scope.launch(defaultDispatcher) {

            while (isActive) {

                if (hasRunningCompletedTimers()) {

                    val updatedCompletedTimers = updateCompletedTimers()

                    if (updatedCompletedTimers.isEmpty()) {
                        stopCompletedTimerUpdates()
                        onTimerCompleted()
                    } else {
                        onTimerUpdated(updatedCompletedTimers.first())
                    }
                } else {
                    stopCompletedTimerUpdates()
                }

                delay(1_000L)

            }
        }
    }

/*    override fun startCompletedTimerUpdates(
        scope: CoroutineScope,
        hasRunningCompletedTimers: () -> Boolean,
        onTimerUpdated: () -> Unit,
        onTimerCompleted: () -> Unit
    ) {
        if (completedJob != null) return

        completedJob = scope.launch(defaultDispatcher) {

            while (isActive) {

                if (hasRunningCompletedTimers()) {
                    onTimerUpdated()
                } else {
                    onTimerCompleted()
                    stopCompletedTimerUpdates()
                }

                delay(1_000L)

            }
        }
    }*/

    /**
     * Stops the background job that updates active timers.
     */
    override fun stopActiveTimerUpdates() {
        activeJob?.cancel()
        activeJob = null
    }

    /**
     * Stops the background job that updates completed timers.
     */
    override fun stopCompletedTimerUpdates() {
        completedJob?.cancel()
        completedJob = null
    }

    /**
     * Stops both active and completed timer update jobs.
     */
    override fun stopAllJobs() {
        stopActiveTimerUpdates()
        stopCompletedTimerUpdates()
    }
}
