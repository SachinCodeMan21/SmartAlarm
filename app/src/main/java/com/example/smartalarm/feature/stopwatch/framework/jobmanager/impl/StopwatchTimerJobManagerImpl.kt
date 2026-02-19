package com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl

import android.util.Log
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.StopwatchTickerJobManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Implementation of [StopwatchTickerJobManager] that manages a coroutine-based timer job
 * for updating the stopwatch state at regular intervals.
 *
 * @property stopwatchUseCase Provides stopwatch-related operations such as updating lap times.
 * @property clockProvider Supplies the current elapsed real time for calculating stopwatch progress.
 */
class StopwatchTimerJobManagerImpl @Inject constructor(
    private val stopwatchUseCase: StopwatchUseCases,
    private val clockProvider: SystemClockHelper,
) : StopwatchTickerJobManager
{

    /**
     * The job responsible for periodically updating the stopwatch.
     * It is cancelled when [stop] is called or when the stopwatch is no longer running.
     */
    private var updateJob: Job? = null

    /**
     * Starts a coroutine that periodically updates the stopwatch state while it is running.
     *
     * @param scope The [CoroutineScope] in which the timer coroutine should be launched.
     *              Typically, this should be tied to a ViewModel or lifecycle-aware component.
     * @param getCurrentStopwatch A lambda that returns the current [StopwatchModel] instance.
     *                            Used to retrieve the latest stopwatch state on each loop iteration.
     * @param onUpdate A callback that is invoked with the updated [StopwatchModel] after each tick.
     *
     * This function does nothing if a job is already active. The job stops automatically
     * if the stopwatch is no longer running (i.e., [StopwatchModel] becomes false).
     */
    override fun start(
        scope: CoroutineScope,
        getCurrentStopwatch: suspend () -> StopwatchModel,
        updateInterval : Long,
        onUpdate: suspend (StopwatchModel) -> Unit
    ) {

        Log.d("TAG","StopwatchTimerJobManagerImpl start executed")


        if (updateJob != null) return

        Log.d("TAG","StopwatchTimerJobManagerImpl start executed when it was not already running ")


        updateJob = scope.launch {

          /*  while (isActive) {

                val currentStopwatch = getCurrentStopwatch()
                if (!currentStopwatch.isRunning) break

                val elapsedTime = clockProvider.getCurrentTime() - currentStopwatch.startTime
                val updatedLapTimes = stopwatchUseCase.updateLapTimes(currentStopwatch)

                val updatedStopwatch = currentStopwatch.copy(
                    elapsedTime = elapsedTime,
                    lapTimes = updatedLapTimes
                )

                onUpdate(updatedStopwatch)

                delay(updateInterval)
            }*/
        }
    }

    /**
     * Stops the currently running stopwatch update job, if any.
     *
     * Cancels the active coroutine and resets the job reference to null.
     */
    override fun stop() {
        Log.d("TAG","StopwatchTimerJobManagerImpl stop executed")
        updateJob?.cancel()
        updateJob = null
    }

}