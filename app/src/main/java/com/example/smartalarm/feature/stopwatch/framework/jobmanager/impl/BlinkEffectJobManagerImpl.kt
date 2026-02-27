package com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl

import com.example.smartalarm.core.framework.di.annotations.DefaultDispatcher
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Default implementation of [BlinkEffectJobManager] that controls a blinking effect using a coroutine.
 *
 * It toggles a visibility flag at fixed intervals (500ms) and invokes a callback with the new state.
 * Typically used to blink a UI component such as a timer display.
 *
 * Uses a coroutine job to manage the blinking lifecycle.
 */
class BlinkEffectJobManagerImpl @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BlinkEffectJobManager {

    /** Coroutine job responsible for running the blinking loop. */
    private var blinkingJob: Job? = null

    /**
     * Starts the blinking coroutine if it's not already active.
     *
     * @param scope The [kotlinx.coroutines.CoroutineScope] in which the blinking coroutine should run (typically ViewModelScope).
     * @param onVisibilityChanged Callback invoked every 500ms with the current visibility state.
     */
    override fun startBlinking(scope: CoroutineScope, onVisibilityChanged: (isVisible: Boolean) -> Unit) {
        if (blinkingJob?.isActive == true) return

        blinkingJob = scope.launch(defaultDispatcher) {

            var isVisible = true

            while (isActive) {
                onVisibilityChanged(isVisible)
                isVisible = !isVisible
                delay(500)
            }
        }
    }

    /**
     * Stops the blinking coroutine if it is currently running, and ensures the visibility is set to `true`.
     */
    override fun stopBlinking() {
        if (blinkingJob?.isActive != true) return
        blinkingJob?.cancel()
        blinkingJob = null
    }
}