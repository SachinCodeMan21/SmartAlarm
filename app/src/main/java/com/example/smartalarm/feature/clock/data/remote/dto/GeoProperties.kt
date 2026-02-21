package com.example.smartalarm.feature.clock.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Contains the main properties of a place returned by the GeoApify API.
 *
 * @property formatted The full formatted address of the place.
 * @property city The city name, if available.
 * @property country The country name.
 * @property lat Latitude of the place.
 * @property lon Longitude of the place.
 * @property timezone Timezone information for the place.
 * @property placeId Unique identifier for the place in GeoApify.
 */
data class GeoProperties(
    val formatted: String,
    val city: String?,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: GeoTimezone,
    @SerializedName("place_id") val placeId: String
)
