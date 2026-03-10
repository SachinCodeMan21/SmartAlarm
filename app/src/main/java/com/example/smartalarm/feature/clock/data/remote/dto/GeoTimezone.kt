package com.example.smartalarm.feature.clock.data.remote.dto

import com.google.gson.annotations.SerializedName


data class GeoTimezone(
    val name: String, // Timezone name (e.g., "America/New_York")
    @SerializedName("offset_STD") val offsetStd: String, // Standard offset (e.g., "-05:00")
    @SerializedName("offset_STD_seconds") val offsetStdSeconds: Int, // Standard offset in seconds (e.g., -18000)
    @SerializedName("offset_DST") val offsetDst: String?, // Daylight saving time offset (optional, e.g., "-04:00")
    @SerializedName("offset_DST_seconds") val offsetDstSeconds: Int? // Daylight saving time offset in seconds (optional, e.g., -14400)
)