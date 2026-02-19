package com.example.smartalarm.feature.clock.data.remote.dto.googlePlaces

import com.example.smartalarm.feature.clock.data.remote.dto.googlePlaces.Prediction
import com.google.gson.annotations.SerializedName

/**
 * Represents the response from the Google Places Autocomplete API.
 *
 * @property predictions A list of suggested places based on the user input.
 * @property status Status of the API response. Expected values: "OK", "ZERO_RESULTS", etc.
 */
data class PlacesResponse(
    @SerializedName("predictions") val predictions: List<Prediction>,
    @SerializedName("status") val status: String
)