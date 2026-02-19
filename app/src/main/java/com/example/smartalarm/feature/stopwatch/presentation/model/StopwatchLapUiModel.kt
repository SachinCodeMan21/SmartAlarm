package com.example.smartalarm.feature.stopwatch.presentation.model

/**
 * UI model representing a single stopwatch lap.
 *
 * @property formattedLapIndex Display text for the lap index (e.g., "#Lap 1").
 * @property formattedLapStartTime Formatted string representing the start time of the lap (e.g., "00:10.25").
 * @property formattedLapElapsedTime Formatted string representing the duration of the lap (e.g., "00:10.25").
 * @property formattedLapEndTime Formatted string representing the end time of the lap (e.g., "00:20.50").
 */
data class StopwatchLapUiModel(
    val formattedLapIndex: String,
    val formattedLapStartTime: String,
    val formattedLapElapsedTime: String,
    val formattedLapEndTime: String
)