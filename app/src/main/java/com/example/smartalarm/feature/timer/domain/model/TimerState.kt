package com.example.smartalarm.feature.timer.domain.model

/**
 * Enum representing the various states of a timer.
 *
 * The timer can be in one of the following states:
 *
 * - [IDLE]: The timer has not started yet or has been reset.
 * - [RUNNING]: The timer is currently running and counting down.
 * - [PAUSED]: The timer is paused, and the countdown is temporarily halted.
 * - [STOPPED]: The timer has stopped, either after reaching the target time or being manually stopped.
 */
enum class TimerState {
    IDLE, RUNNING, PAUSED, STOPPED
}
