package com.example.smartalarm.feature.clock.data.remote.dto.googlePlaces

import com.google.gson.annotations.SerializedName

/**
 * Represents a single place prediction result from the Google Places Autocomplete API.
 *
 * @property placeId The unique identifier of the predicted place.
 * @property description A full description of the place (e.g., "Mumbai, Maharashtra, India").
 * @property structuredFormatting Contains structured display information for the prediction.
 */
data class Prediction(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("description") val description: String,
    @SerializedName("structured_formatting") val structuredFormatting: StructuredFormatting
)
