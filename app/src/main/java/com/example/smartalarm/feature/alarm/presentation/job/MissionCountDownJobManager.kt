package com.example.smartalarm.feature.alarm.presentation.job

import kotlinx.coroutines.CoroutineScope

/**
 * Interface for managing a countdown timer using Kotlin coroutines.
 *
 * This interface defines methods to start and stop a countdown timer, typically used for mission-related
 * time constraints in mini-games or task-based user interactions. It provides tick updates and a finish callback.
 */
interface MissionCountDownJobManager {

    /**
     * Starts a countdown timer for the given duration.
     *
     * This function launches a countdown in the provided [scope], calling [onTick] at 1-second intervals
     * with the remaining progress as a percentage (from 100 down to 0).
     * Once the countdown completes, [onFinish] is invoked.
     *
     * If a countdown is already running, it should be canceled before starting a new one.
     *
     * @param scope The [CoroutineScope] in which the countdown will be executed.
     * @param targetDuration The total duration of the countdown, in milliseconds.
     * @param onTick Callback invoked each second with the remaining time as a percentage (0–100).
     * @param onFinish Callback invoked once the countdown reaches zero.
     */
    fun startCountdown(
        scope: CoroutineScope,
        targetDuration : Long,
        onTick: (Int) -> Unit,
        onFinish: () -> Unit
    )

    /**
     * Stops the currently running countdown timer, if any.
     *
     * Cancels the underlying coroutine or timer logic. This is safe to call even if no countdown is active.
     */
    fun stopCountdown()
}
