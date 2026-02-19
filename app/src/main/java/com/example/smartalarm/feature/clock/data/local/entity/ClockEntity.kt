package com.example.smartalarm.feature.clock.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a user-selected time zone entry for the clock screen.
 *
 * This data is stored in the `clock_table` and reflects the time zones the user
 * has chosen to monitor.
 *
 * Each row corresponds to a unique place/time zone and includes both
 * time zone metadata and display names.
 *
 * @property id Auto-generated primary key for identifying the clock entry uniquely.
 * @property fullName The full descriptive name of the place (e.g., "New York, USA").
 * @property primaryName A concise label used in the UI (e.g., "New York").
 * @property timeZoneId The canonical ID of the time zone (e.g., "America/New_York").
 * @property offsetSeconds Total offset from UTC in seconds (raw + DST).
 * @property currentTime The current local time for the place, formatted (e.g., "03:25 PM").
 */
@Entity(tableName = "clock_table")
data class ClockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val primaryName: String,
    val timeZoneId: String,
    val offsetSeconds: Long,
    val currentTime: String
)
