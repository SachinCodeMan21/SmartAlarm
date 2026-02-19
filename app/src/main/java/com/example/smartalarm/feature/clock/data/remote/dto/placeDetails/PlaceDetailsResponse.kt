package com.example.smartalarm.feature.clock.data.remote.dto.placeDetails

import com.google.gson.annotations.SerializedName

/**
 * Represents the response from the Google Place Details API.
 *
 * @property result The main data payload, containing location details.
 * @property status Status of the API call (e.g., "OK", "INVALID_REQUEST").
 */
data class PlaceDetailsResponse(
    @SerializedName("result")
    val result: PlaceDetailResult,

    @SerializedName("status")
    val status: String,
)
