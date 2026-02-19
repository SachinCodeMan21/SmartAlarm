package com.example.smartalarm.feature.clock.data.remote.dto.googlePlaces

import com.google.gson.annotations.SerializedName

/**
 * Represents structured display formatting for a prediction.
 *
 * @property primaryText The main text to be displayed (e.g., "Mumbai").
 * @property secondaryText Additional context for the place (e.g., "Maharashtra, India").
 */
data class StructuredFormatting(
    @SerializedName("main_text") val primaryText: String,
    @SerializedName("secondary_text") val secondaryText: String
)