package com.example.smartalarm.feature.clock.domain.model

/**
 * Data model representing a place and its associated time zone information.
 *
 * This model is used to display place search results, store user-selected places,
 * and retrieve relevant time zone details.
 *
 * @property id Unique identifier for the place (typically from local storage or database).
 * @property fullName Full formatted name of the place (e.g., "New York, United States").
 * @property primaryName Primary or display name of the place (e.g., "New York").
 * @property timeZoneId Identifier for the place's time zone (e.g., "America/New_York").
 * @property offsetSeconds UTC offset in seconds (e.g., -18000 for UTC-5:00).
 * @property currentTime The current local time at the place, formatted as a string.
 */
data class PlaceModel(
    val id : Long = 0,
    val fullName: String,
    val primaryName: String,
    val timeZoneId: String,
    val offsetSeconds: Long,
    val currentTime: String
)
