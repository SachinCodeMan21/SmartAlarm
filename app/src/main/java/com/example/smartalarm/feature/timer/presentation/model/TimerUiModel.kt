package com.example.smartalarm.feature.timer.presentation.model

/**
 * Represents the UI state for the Timer screen.
 *
 * This data class is used by the ViewModel to provide the current state of the UI to the Fragment.
 * It includes properties that control the display of the timer, the start button, and the delete timer button.
 *
 * @property formattedTime The current time displayed or entered on the timer, formatted as "HH:mm:ss".
 * @property isStartButtonVisible A flag indicating whether the "Start Timer" button should be visible.
 * @property isDeleteTimerButtonVisible A flag indicating whether the "Delete Timer" button should be visible,
 * based on whether there are any active timers running.
 */
data class TimerUiModel(
    val formattedTime: String = "00h : 00m : 00s",
    val isStartButtonVisible: Boolean = false,
    val isDeleteTimerButtonVisible: Boolean = false
)
