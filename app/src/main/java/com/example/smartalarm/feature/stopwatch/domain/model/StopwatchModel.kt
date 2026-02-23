package com.example.smartalarm.feature.stopwatch.domain.model

/**
 * Domain model representing a stopwatch instance with its state and lap data.
 *
 * Encapsulates stopwatch timing information, running state, and lap records.
 * Used in the business logic layer and mapped to UI models as needed.
 *
 * @property startTime Timestamp (in milliseconds) when the stopwatch was started.
 * @property endTime Timestamp (in milliseconds) when the stopwatch was paused or stopped.
 * @property elapsedTime Total elapsed time (in milliseconds) of the stopwatch session.
 * @property isRunning Indicates whether the stopwatch is currently running.
 * @property lapTimes List of recorded lap details.
 * @property lapCount Total number of recorded laps.
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
     * Duration of the last completed lap in milliseconds.
     *
     * - Returns the second-to-last lap duration if two or more laps exist.
     * - Returns total elapsed time if only one lap exists.
     * - Returns 0 if no laps exist.
     */
    val getLastLapDuration: Long get() {
        return when {
            lapTimes.size >= 2 -> lapTimes[lapTimes.size - 2].lapElapsedTimeMillis
            lapTimes.size == 1 -> elapsedTime
            else -> 0L
        }
    }

    /**
     * Progress of the current lap as a percentage (0 to 100) relative to the last lap duration.
     *
     * - Calculated by comparing current lap elapsed time with the duration of the last completed lap.
     * - Returns 0 if no valid lap duration is available to compare against.
     */
    val getIndicatorProgress: Int get() {
            val lastDuration = getLastLapDuration.takeIf { it > 0L } ?: return 0
            val currentStart = lapTimes.maxByOrNull { it.lapIndex }?.lapStartTimeMillis ?: 0L
            val elapsed = elapsedTime - currentStart
            return ((elapsed * 100) / lastDuration).toInt().coerceIn(0, 100)
        }

}
