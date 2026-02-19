package com.example.smartalarm.feature.clock.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a place with associated time zone information in the database.
 * This entity stores information about the place's name, time zone, offset from UTC,
 * and the current time in that location.
 *
 * @property id The unique identifier for the place (auto-generated).
 * @property fullName The full name of the place (e.g., "New York, USA").
 * @property primaryName The primary name of the place (e.g., "New York").
 * @property timeZoneId The ID of the time zone for this place (e.g., "America/New_York").
 * @property offsetSeconds The offset from UTC in seconds (e.g., UTC-5 hours would be -18000).
 * @property currentTime The current local time in the place, formatted as a string (e.g., "12:45 PM").
 *
 * @constructor Creates an instance of [PlaceEntity] with the specified properties.
 */
@Entity(tableName = "searched_places_table")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "primary_name") val primaryName: String,
    @ColumnInfo(name = "time_zone_id") val timeZoneId: String,
    @ColumnInfo(name = "offset_seconds") val offsetSeconds: Long,
    @ColumnInfo(name = "current_time") val currentTime: String
)
