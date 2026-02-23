package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents the persistent state of the Stopwatch feature.
 *
 * ### Why this exists:
 * Unlike a standard log table, this entity acts as a **State Snapshot**. It allows the
 * stopwatch to remain consistent across configuration changes (rotations), process
 * death (low memory), and device reboots.
 *
 * ### Instead of:
 * 1. **In-memory variables:** Which would be wiped as soon as the user clears the
 * app from Recents.
 * 2. **SharedPreferences:** While simpler, using Room provides a single source of
 * truth for all Clock features (Alarms, World Clock, Stopwatch) and allows for
 * atomic updates alongside [StopwatchLapEntity] entries.
 * 3. **Multiple Rows:** Since the app supports only one active stopwatch instance,
 * this table is constrained to a single row (ID = 1).
 *
 * @property id Constant primary key to enforce a single-row (singleton) state.
 * @property startTimeMillis The system clock time (bootTime) when the stopwatch was first started.
 * @property elapsedTimeMillis The accumulated time (in ms) before the last pause/stop.
 * @property lastStoppedAt The system clock time when the stopwatch was paused; used to
 * calculate remaining time upon resume.
 * @property isRunning Flag to determine if the UI should continue the tick animation.
 * @property totalLaps Cache of the total number of laps to avoid counting logic in the UI layer.
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