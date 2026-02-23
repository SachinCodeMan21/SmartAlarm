package com.example.smartalarm.feature.stopwatch.domain.model

/**
 * A pure domain representation of a single stopwatch lap.
 *
 * ### Architectural Role:
 * This model is "Database Agnostic." It contains no references to SQLite primary keys
 * or foreign keys. Instead, it focuses entirely on the business logic of a lap:
 * when it started, how long it lasted, and its position in the sequence.
 *
 * ### UI & Identity:
 * Because this model lacks a database ID, consumers (such as RecyclerView adapters)
 * should use the [lapIndex] as a stable identifier for diffing and animations,
 * as it is unique within the context of a single stopwatch session.
 */
data class StopwatchLapModel(
    /**
     * The sequential position of the lap (e.g., Lap 1, Lap 2).
     * This serves as the primary identifier for the domain and UI layers.
     */
    val lapIndex: Int,

    /**
     * The absolute system time (in milliseconds) when this specific lap was started.
     */
    val lapStartTimeMillis: Long,

    /**
     * The total duration of this lap in milliseconds.
     */
    val lapElapsedTimeMillis: Long,

    /**
     * The absolute system time (in milliseconds) when this lap was completed.
     * If the lap is currently active, this value may represent the last recorded tick.
     */
    val lapEndTimeMillis: Long
)