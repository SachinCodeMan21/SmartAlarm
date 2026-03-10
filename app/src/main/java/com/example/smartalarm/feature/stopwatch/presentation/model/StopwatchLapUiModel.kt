package com.example.smartalarm.feature.stopwatch.presentation.model

/**
 * UI-specific representation of a stopwatch lap interval.
 *
 * This model serves as a raw data carrier for the Presentation layer. It remains
 * "formatting agnostic," holding only the numerical metrics required for a lap.
 * Final visual representation (e.g., converting milliseconds to "00:00") is
 * delegated to the UI layer or specialized formatters.
 */
data class StopwatchLapUiModel(
    val lapIndex: Int,
    val lapStartTimeMillis: Long,
    val lapElapsedMillis: Long,
    val lapEndTimeMillis: Long
)