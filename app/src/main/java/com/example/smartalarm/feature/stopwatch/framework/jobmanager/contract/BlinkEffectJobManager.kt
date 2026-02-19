package com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract

import kotlinx.coroutines.CoroutineScope

/**
 * Interface for controlling a blinking UI effect (such as blinking stopwatch digits) using coroutines.
 *
 * Typically used to indicate a paused or inactive state visually by toggling visibility at intervals.
 */
interface BlinkEffectJobManager {

    /**
     * Starts the blinking effect within the provided [scope].
     *
     * The visibility is toggled at a fixed interval (e.g., every 500ms),
     * and the [onVisibilityChanged] callback is invoked with the new visibility state.
     *
     * @param scope The [kotlinx.coroutines.CoroutineScope] in which the blinking coroutine should run.
     * @param onVisibilityChanged Callback invoked with `true` or `false` when visibility changes.
     */
    fun startBlinking(scope: CoroutineScope, onVisibilityChanged: (isVisible: Boolean) -> Unit)


    /**
     * Stops the blinking effect and invokes the  callback to restore final visibility.
     */
    fun stopBlinking()

}