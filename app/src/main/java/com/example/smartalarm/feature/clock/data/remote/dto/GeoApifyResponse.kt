package com.example.smartalarm.feature.clock.data.remote.dto

/**
 * Represents the full response from the GeoApify API for place queries.
 *
 * @property features The list of features (places) returned by the API.
 * Each feature contains detailed information like formatted address, city,
 * coordinates, and timezone.
 */
data class GeoApifyResponse(
    val features: List<GeoFeature>
)