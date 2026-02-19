package com.example.smartalarm.feature.timer.presentation.event

import com.example.smartalarm.feature.timer.presentation.viewmodel.TimerViewModel

/**
 * Represents the various user actions and system events that can occur on the Timer screen.
 *
 * This sealed class encapsulates UI events triggered by the user or the system,
 * enabling the [TimerViewModel] to handle them in a type-safe and centralized manner.
 */
sealed class TimerEvent {

    /**
     * Initializes the Timer UI state when the screen becomes visible (e.g., when the fragment starts).
     *
     * This event is dispatched during the `onStart()` lifecycle method to load all the timers set by the user.
     * Based on the available timers, it decides whether the "Delete Timer" button should be visible or not
     * and updates the UI to reflect the initial state.
     */
    object InitTimerUIState : TimerEvent()

    /**
     * Triggered when the user clicks any keypad button (e.g., "1", "0", "⌫").
     *
     * This event captures the label of the keypad button pressed and appends it to the timer input in the ViewModel.
     * The UI is updated accordingly to reflect the entered digits or perform actions like backspace when necessary.
     *
     * @param label The label of the keypad button that was clicked (e.g., "1", "0", "⌫").
     */
    data class HandleKeypadClick(val label: String) : TimerEvent()

    /**
     * Triggered when the user clicks the "Start Timer" button.
     *
     * This event processes the input entered by the user, creates a new timer, and saves it.
     * Afterward, the user is navigated to the "Show Timer" screen, where the newly created timer is displayed.
     */
    object HandleStartTimerClick : TimerEvent()

    /**
     * Triggered when the user clicks the "Delete Timer" button.
     *
     * This event navigates the user to the "Show Timer" screen, where they can view all active timers
     * and delete the ones they no longer need.
     */
    object HandleDeleteTimerClick : TimerEvent()

}
