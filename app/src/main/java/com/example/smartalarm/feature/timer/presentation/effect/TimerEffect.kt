package com.example.smartalarm.feature.timer.presentation.effect

/**
 * Represents one-time UI effects emitted by the TimerViewModel to be handled by the UI layer.
 *
 * These effects are typically transient events such as navigation or showing messages,
 * which should not be persisted in UI state.
 */
sealed class TimerEffect {

    /**
     * Effect to navigate from the current screen to the Show Timer screen.
     *
     * Used to trigger navigation events after successful timer operations.
     */
    object NavigateToShowTimerScreen : TimerEffect()

    /**
     * Effect to show a SnackBar message to the user.
     *
     * @property message The text message to display in the SnackBar.
     */
    data class ShowSnackBar(val message: String) : TimerEffect()

}


