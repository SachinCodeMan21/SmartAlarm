package com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.CoroutineScope

/**
 * A contract for managing a coroutine-based stopwatch timer job.
 *
 * This interface defines methods for starting and stopping a recurring job that updates
 * the stopwatch's elapsed time and lap information while it is running.
 *
 * Implementations should ensure proper coroutine lifecycle management, especially
 * when working within UI-related scopes such as a ViewModel.
 */
interface StopwatchTickerJobManager {

    /**
     * Starts the stopwatch update job.
     *
     * This function launches a coroutine in the provided [scope] that periodically checks the stopwatch's state
     * using [getCurrentStopwatch], calculates the elapsed time, updates the lap times, and invokes the [onUpdate] callback
     * with the modified [StopwatchModel] instance containing the updated elapsed time and lap times.
     *
     * The coroutine runs continuously as long as the stopwatch is in a running state ([StopwatchModel.isRunning] == true).
     * It will automatically be canceled if the stopwatch is paused, reset, or when [stop] is called.
     *
     * @param scope The [CoroutineScope] in which the timer coroutine will run. This should generally be tied to a lifecycle-aware
     *              component, such as a ViewModel or a Service, to ensure the job is properly managed and canceled when appropriate.
     * @param getCurrentStopwatch A lambda that returns the current [StopwatchModel] state on each tick. This ensures that the stopwatch state
     *                            used in calculations is always up-to-date.
     * @param onUpdate A callback that is invoked with the updated [StopwatchModel] containing the new elapsed time and lap times.
     *                 This is typically used to update UI state or trigger any further operations related to the stopwatch.
     */
    fun start(
        scope: CoroutineScope,
        getCurrentStopwatch: suspend () -> StopwatchModel,
        updateInterval : Long,
        onUpdate: suspend (StopwatchModel) -> Unit
    )

    /**
     * Stops the currently active stopwatch update job, if any.
     *
     * This cancels the internal coroutine and releases any held resources. No further updates
     * will be emitted after this call unless [start] is invoked again.
     */
    fun stop()
}