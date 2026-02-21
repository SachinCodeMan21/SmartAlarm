package com.example.smartalarm.feature.clock.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents the timezone information for a place.
 *
 * @property name The IANA timezone identifier (e.g., "Asia/Kolkata").
 * @property offsetStd The standard UTC offset as a string (e.g., "+05:30").
 * @property offsetSeconds The standard UTC offset in seconds.
 */
data class GeoTimezone(
    val name: String,
    @SerializedName("offset_STD") val offsetStd: String,
    @SerializedName("offset_STD_seconds") val offsetSeconds: Int
)