package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Persistent state snapshot for the Stopwatch feature.
 *
 * Unlike a traditional log table, this entity stores the current runtime state
 * of the stopwatch so it can be restored after configuration changes,
 * process death, or device restarts.
 *
 * Design decisions:
 * - Stored in Room instead of memory or SharedPreferences to provide a single
 *   source of truth for clock-related features.
 * - Only a single row is allowed (id = 1) since the app supports one stopwatch instance.
 * - Designed to work alongside [StopwatchLapEntity] for atomic updates.
 *
 * @property id Constant primary key enforcing a single-row table.
 * @property startTimeMillis Boot-time timestamp when the stopwatch started.
 * @property elapsedTimeMillis Accumulated elapsed time before the last pause.
 * @property lastStoppedAt Timestamp when the stopwatch was last paused.
 * @property isRunning Indicates whether the stopwatch is currently active.
 * @property totalLaps Cached lap count to avoid UI-layer calculations.
 */
@Entity(tableName = "stopwatch_state")
data class StopwatchStateEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int = 1,

    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long = 0L,

    @ColumnInfo(name = "elapsed_time_millis")
    val elapsedTimeMillis: Long = 0L,

    @ColumnInfo(name = "last_stopped_at")
    val lastStoppedAt: Long = 0L,

    @ColumnInfo(name = "is_running")
    val isRunning: Boolean = false,

    @ColumnInfo(name = "total_laps")
    val totalLaps: Int = 0
)