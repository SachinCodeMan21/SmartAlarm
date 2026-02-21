package com.example.smartalarm.feature.clock.data.remote.dto

/**
 * Wraps a single feature returned by the GeoApify API.
 *
 * @property properties The properties of the place.
 */
data class GeoFeature(
    val properties: GeoProperties
)
