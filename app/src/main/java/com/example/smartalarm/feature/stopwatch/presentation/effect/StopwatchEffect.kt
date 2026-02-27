package com.example.smartalarm.feature.stopwatch.presentation.effect

import com.example.smartalarm.core.utility.exception.DataError

/**
 * Represents one-time side effects emitted by the stopwatch feature.
 *
 * Effects are used to trigger UI actions or platform interactions that
 * should not be part of the persistent state, such as permission requests,
 * foreground service control, UI animations, and user-facing messages.
 */
sealed class StopwatchEffect {

    /**
     * Effect to change the visibility of blinking UI elements (e.g., stopwatch timer).
     * Commonly used when stopwatch is paused and blinking should indicate inactive state.
     *
     * @property isVisible Whether the blinking UI element should be visible.
     */
    data class BlinkVisibilityChanged(val isVisible: Boolean) : StopwatchEffect()

    /**
     * Effect to show an error message to the user (e.g., via a toast or snackBar).
     *
     * @property error The error message to display.
     */
    data class ShowError(val error: DataError) : StopwatchEffect()

    /**
     * Effect to start the stopwatch foreground service.
     * Typically triggered when the app goes to background but the stopwatch is still running.
     */
    data object StartForegroundService : StopwatchEffect()

    /**
     * Effect to stop the stopwatch foreground service.
     * Typically triggered when the stopwatch is paused or reset, and no background service is needed.
     */
    data object StopForegroundService : StopwatchEffect()


}