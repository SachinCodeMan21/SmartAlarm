package com.example.smartalarm.feature.stopwatch.presentation.event

/**
 * Represents user and lifecycle-driven events for the stopwatch feature.
 *
 * This sealed class includes:
 * - User interactions (start/pause, reset, record lap).
 * - UI-related control events (blinking start/stop).
 * - App visibility transitions (entering foreground or background).
 *
 * Designed for use in an MVI-style architecture where the ViewModel remains
 * platform-agnostic and reacts to explicit intents.
 */
sealed class StopwatchEvent {

    /**
     * Event to toggle the stopwatch run state.
     * Starts the stopwatch if it's stopped, or stops it if it's running.
     */
    data object ToggleRunState : StopwatchEvent()

    /**
     * Event to reset the stopwatch to its initial state, clearing time and laps.
     */
    data object ResetStopwatch : StopwatchEvent()

    /**
     * Event to record the current lap time while the stopwatch is running.
     */
    data object RecordStopwatchLap : StopwatchEvent()

    /** Event indicating the stopwatch should move to the background. */
    data object MoveToBackground : StopwatchEvent()

}
