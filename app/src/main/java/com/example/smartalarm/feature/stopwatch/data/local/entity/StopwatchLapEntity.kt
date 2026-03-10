package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single lap recorded during a stopwatch session.
 *
 * Laps are stored in a separate table to maintain a normalized database schema
 * and allow efficient querying without affecting the main stopwatch state.
 *
 * Design decisions:
 * - Linked to [StopwatchStateEntity] via a foreign key (`stopwatch_id`).
 * - CASCADE deletion ensures laps are automatically removed when the parent
 *   stopwatch state is cleared, preserving referential integrity.
 * - Uses a surrogate primary key ([id]) instead of `lapIndex` to provide
 *   stable identifiers for RecyclerView DiffUtil updates.
 * - All time values are stored in milliseconds (`*_millis`) to maintain
 *   consistent units across the data, repository, and UI layers.
 *
 * @property id Auto-generated unique identifier for the lap record.
 * @property stopwatchId Reference to the parent stopwatch instance.
 * @property lapIndex Sequential lap number used for display.
 * @property lapStartTimeMillis Timestamp when the lap started.
 * @property lapElapsedTimeMillis Duration of the lap in milliseconds.
 * @property lapEndTimeMillis Timestamp when the lap ended.
 */
@Entity(
    tableName = "stopwatch_laps",
    foreignKeys = [
        ForeignKey(
            entity = StopwatchStateEntity::class,
            parentColumns = ["id"],
            childColumns = ["stopwatch_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stopwatch_id")]
)
data class StopwatchLapEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "stopwatch_id")
    val stopwatchId: Int,

    @ColumnInfo(name = "lap_index")
    val lapIndex: Int,

    @ColumnInfo(name = "lap_start_time_millis")
    val lapStartTimeMillis: Long,

    @ColumnInfo(name = "lap_elapsed_time_millis")
    val lapElapsedTimeMillis: Long,

    @ColumnInfo(name = "lap_end_time_millis")
    val lapEndTimeMillis: Long
)