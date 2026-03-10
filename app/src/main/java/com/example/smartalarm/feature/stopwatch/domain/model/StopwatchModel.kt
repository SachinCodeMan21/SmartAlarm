package com.example.smartalarm.feature.stopwatch.domain.model

/**
 * Immutable domain aggregate representing a complete stopwatch session.
 *
 * This model serves as the primary data structure for business logic and UI state
 * derivation. It encapsulates high-level timing metrics and session metadata
 * without leakage of persistence-layer identifiers.
 *
 * @property startTime Unix timestamp (ms) marking the initial start of the session.
 * @property elapsedTime Aggregate duration (ms) the stopwatch has been active.
 * @property endTime Unix timestamp (ms) marking the most recent pause or termination.
 * @property isRunning Current operational state of the stopwatch engine.
 * @property lapTimes Immutable collection of recorded [StopwatchLapModel] entries.
 * @property lapCount Cached count of the total laps for rapid O(1) access.
 */
data class StopwatchModel(
    val startTime: Long = 0L,
    val elapsedTime: Long = 0L,
    val endTime: Long = 0L,
    val isRunning: Boolean = false,
    val lapTimes: List<StopwatchLapModel> = emptyList(),
    val lapCount: Int = 0,
) {

    /**
     * Derives the duration of the previous completed lap.
     * Used as a benchmark for calculating current lap performance and progress.
     *
     * @return Duration in milliseconds. Returns [elapsedTime] for the initial lap
     * or 0L if no session data exists.
     */
    val getLastLapDuration: Long get() {
        return when {
            lapTimes.size >= 2 -> lapTimes[lapTimes.size - 2].lapElapsedTimeMillis
            lapTimes.size == 1 -> elapsedTime
            else -> 0L
        }
    }

    /**
     * Calculates the current lap's progress as a normalized percentage (0-100).
     * * This value is relative to the previous lap's performance and is intended
     * for driving high-frequency UI components like progress indicators.
     *
     * @return Integer percentage coerced within the 0..100 range.
     */
    val getIndicatorProgress: Int get() {
        val lastDuration = getLastLapDuration.takeIf { it > 0L } ?: return 0
        val currentStart = lapTimes.maxByOrNull { it.lapIndex }?.lapStartTimeMillis ?: 0L
        val elapsed = elapsedTime - currentStart
        return ((elapsed * 100) / lastDuration).toInt().coerceIn(0, 100)
    }
}