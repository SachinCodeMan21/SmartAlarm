package com.example.smartalarm.feature.clock.data.remote.dto.placeDetails

/**
 * Wraps the result from the Place Details API response.
 *
 * @property geometry Contains geographic coordinates and geometry info of the place.
 */
data class PlaceDetailResult(
    val geometry: Geometry,
)