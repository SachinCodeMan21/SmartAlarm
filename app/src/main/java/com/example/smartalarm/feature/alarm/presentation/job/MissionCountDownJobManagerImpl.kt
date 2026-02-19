package com.example.smartalarm.feature.alarm.presentation.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Implementation of [MissionCountDownJobManager] that manages a 60-second countdown timer using Kotlin coroutines.
 *
 * This class is responsible for starting and stopping a coroutine-based countdown timer, typically used in
 * time-sensitive mini-games or UI flows. The countdown runs for 60 seconds and provides per-second progress updates.
 *
 * The timer reports progress as a percentage from 100 to 0 and invokes a callback when the countdown completes.
 *
 * @constructor Creates an instance of [MissionCountDownJobManagerImpl].
 */
class MissionCountDownJobManagerImpl @Inject constructor() : MissionCountDownJobManager {

    /**
     * Reference to the currently running countdown coroutine job, if any.
     */
    private var job: Job? = null

    /**
     * Starts a countdown timer that emits progress updates at 1-second intervals.
     *
     * - The countdown runs for the specified [targetDuration] in milliseconds.
     * - On each tick (every second), [onTick] is called with the remaining progress as a percentage (0–100).
     * - When the countdown completes, [onFinish] is invoked.
     * - If a previous countdown is running, it is stopped before starting a new one.
     *
     * @param scope The [CoroutineScope] in which the countdown will run.
     * @param targetDuration The total duration of the countdown in milliseconds.
     * @param onTick Callback invoked on each second with the remaining time as a percentage.
     * @param onFinish Callback invoked once the countdown completes.
     */
    override fun startCountdown(
        scope: CoroutineScope,
        targetDuration: Long, // in milliseconds
        onTick: (Int) -> Unit, // percent (0–100)
        onFinish: () -> Unit
    ) {
        stopCountdown()
        val totalSeconds = targetDuration / 1000
        job = scope.launch {
            var elapsedSeconds = 0L
            while (elapsedSeconds < totalSeconds) {
                val progress = ((totalSeconds - elapsedSeconds).toDouble() / totalSeconds * 100).toInt()
                onTick(progress)
                delay(1000L)
                elapsedSeconds++
            }
            onTick(0) // optional: tick at 0%
            onFinish()
        }
    }


    /**
     * Stops the currently running countdown timer, if any.
     *
     * Cancels the active coroutine job, preventing further progress updates or completion callback.
     * Safe to call even if no timer is active.
     */
    override fun stopCountdown() {
        job?.cancel()
    }
}
