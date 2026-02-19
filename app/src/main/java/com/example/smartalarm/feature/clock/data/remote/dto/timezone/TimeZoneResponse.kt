package com.example.smartalarm.feature.clock.data.remote.dto.timezone

/**
 * Represents the response from the Google Time Zone API.
 *
 * @property dstOffset Daylight savings offset in seconds.
 * @property rawOffset Standard time zone offset from UTC in seconds.
 * @property status Status of the API response (e.g., "OK", "ZERO_RESULTS").
 * @property timeZoneId The IANA time zone ID (e.g., "Asia/Kolkata").
 * @property timeZoneName The human-readable name of the time zone.
 */
data class TimeZoneResponse(
    val dstOffset: Long,
    val rawOffset: Long,
    val status: String,
    val timeZoneId: String,
    val timeZoneName: String,
)