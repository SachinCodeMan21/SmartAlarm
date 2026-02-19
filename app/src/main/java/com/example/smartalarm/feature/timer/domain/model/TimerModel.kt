package com.example.smartalarm.feature.timer.domain.model


/**
 * A data class representing a timer with various properties to manage its state, timing, and control.
 *
 * @property timerId The unique identifier for this timer.
 * @property startTime The timestamp (in milliseconds) when the timer was started.
 * @property remainingTime The remaining time (in milliseconds) for the timer, if it is running or paused.
 * @property endTime The timestamp (in milliseconds) when the timer was supposed to end.
 * @property targetTime The target time (in milliseconds) the timer is set to reach.
 * @property isTimerRunning A boolean flag indicating whether the timer is currently running.
 * @property isTimerSnoozed A boolean flag indicating whether the timer has been snoozed.
 * @property snoozedTargetTime The target time (in milliseconds) to which the timer was snoozed.
 * @property state The current state of the timer, defined by the [TimerState] enum.
 */
data class TimerModel(
    val timerId: Int = 0,
    val startTime: Long = 0,
    val remainingTime: Long = 0,
    val endTime: Long = 0,
    val targetTime: Long = 0,
    val isTimerRunning: Boolean = false,
    val isTimerSnoozed: Boolean = false,
    val snoozedTargetTime: Long = 0,
    val state: TimerState = TimerState.IDLE
)
