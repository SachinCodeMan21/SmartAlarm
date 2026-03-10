package com.example.smartalarm.feature.stopwatch.domain.model

/**
 * Internal domain representation of a specific stopwatch lap interval.
 *
 * This entity is strictly decoupled from persistence schemas. It relies on
 * semantic identification ([lapIndex]) rather than database primary keys,
 * ensuring business logic stability during data migrations or storage shifts.
 */
data class StopwatchLapModel(
    /**
     * Sequential identifier (1-based) representing the lap's order in the session.
     * Acts as the stable ID for UI diffing algorithms (e.g., DiffUtil).
     */
    val lapIndex: Int,

    /**
     * Unix timestamp (ms) representing the precise moment this lap interval began.
     */
    val lapStartTimeMillis: Long,

    /**
     * Total duration (ms) recorded between the start and completion of this lap.
     */
    val lapElapsedTimeMillis: Long,

    /**
     * Unix timestamp (ms) marking the completion of this lap interval.
     */
    val lapEndTimeMillis: Long
)