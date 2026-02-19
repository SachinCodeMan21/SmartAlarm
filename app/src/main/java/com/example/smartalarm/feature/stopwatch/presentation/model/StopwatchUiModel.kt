package com.example.smartalarm.feature.stopwatch.presentation.model

/**
 * UI model representing the full state of the stopwatch screen.
 *
 * @property secondsText Formatted text for the stopwatch's seconds portion (e.g., "00s").
 * @property milliSecondsText Formatted text for the milliseconds portion (e.g., "25").
 * @property isRunning Whether the stopwatch is currently running.
 * @property progress Progress bar value indicating stopwatch progress (0-100 or as defined).
 * @property laps List of recorded laps shown in the UI.
 */
data class StopwatchUiModel(
    val secondsText: String = "",
    val milliSecondsText: String = "",
    val isRunning: Boolean = false,
    val progress: Int = 0,
    val laps: List<StopwatchLapUiModel> = emptyList(),
)