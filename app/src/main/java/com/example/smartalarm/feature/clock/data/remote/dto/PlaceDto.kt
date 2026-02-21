package com.example.smartalarm.feature.clock.data.remote.dto

/**
 * Data Transfer Object representing a place fetched from the remote API.
 *
 * This class encapsulates high-level information about a place including
 * full name, primary name, time zone data, and the current time.
 *
 * This DTO is used to decouple remote API responses from domain or local models.
 *
 * @property fullName The full descriptive name of the place (e.g., "New York, NY, USA").
 * @property primaryName The short or primary name of the place (e.g., "New York").
 * @property timeZoneId The time zone identifier for the place (e.g., "America/New_York").
 * @property offsetSeconds The total UTC offset in seconds including DST.
 * @property currentTime The current local time of the place formatted as a string (e.g., "03:45 PM").
 */
data class PlaceDto(
    val fullName: String,
    val primaryName: String,
    val timeZoneId: String,
    val offsetSeconds: Long,
    val currentTime: String
)