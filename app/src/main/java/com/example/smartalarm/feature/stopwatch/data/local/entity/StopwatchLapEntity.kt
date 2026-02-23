package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an individual lap record associated with a stopwatch session.
 *
 * ### Why this exists:
 * This entity is designed to provide a historical log of time splits. By isolating
 * laps into a separate table rather than a serialized list inside the state entity,
 * we achieve a **normalized database schema**. This allows for efficient querying,
 * independent updates, and virtually unlimited lap counts without impacting the
 * performance of the main state table.
 *
 * ### Architectural Choices:
 * 1. **Foreign Key Mapping:** Linked to [StopwatchStateEntity] via `stopwatch_id`.
 * The `CASCADE` delete ensures that if a stopwatch state is cleared, all
 * associated laps are automatically purged, maintaining referential integrity.
 * 2. **Surrogate Primary Key ([id]):** While `lapIndex` is logically unique per
 * session, a dedicated Long [id] is used to provide stable identifiers for
 * RecyclerView's DiffUtil. This ensures smooth UI animations during list
 * updates and deletions.
 * 3. **Time Normalization:** All duration and timestamp fields are stored in
 * milliseconds (Suffix: `_millis`) to prevent unit-mismatch bugs across
 * the Repository and ViewModel layers.
 *
 * @property id Auto-generated unique identifier used as the primary key for the database row.
 * @property stopwatchId Reference to the parent stopwatch instance this lap belongs to.
 * @property lapIndex The sequential order of the lap (e.g., 1, 2, 3) for display purposes.
 * @property lapStartTimeMillis The system epoch time when this specific lap began.
 * @property lapElapsedTimeMillis The duration of this specific lap (difference between start and end).
 * @property lapEndTimeMillis The system epoch time when the lap button was pressed.
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