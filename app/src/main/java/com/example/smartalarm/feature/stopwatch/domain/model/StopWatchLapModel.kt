package com.example.smartalarm.feature.stopwatch.domain.model

/**
 * Data model representing a single lap in a stopwatch session.
 *
 * @property lapIndex The index or number of the lap (starting from 1).
 * @property lapStartTime The timestamp (in milliseconds) when the lap started.
 * @property lapElapsedTime The duration (in milliseconds) of the lap.
 * @property lapEndTime The timestamp (in milliseconds) when the lap ended.
 */
data class StopWatchLapModel(
    val lapIndex: Int,
    val lapStartTime: Long,
    val lapElapsedTime: Long,
    val lapEndTime: Long
)