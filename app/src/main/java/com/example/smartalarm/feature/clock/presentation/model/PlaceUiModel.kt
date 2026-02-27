package com.example.smartalarm.feature.clock.presentation.model

/**
 * UI model representing a place for display in the global timezone list.
 *
 * All formatting logic (like human-readable time differences) should be applied here.
 *
 * @property id Unique identifier (from domain model).
 * @property name Display name of the place.
 * @property currentTime Current local time at the place (formatted for UI).
 * @property timeDifference Human-readable string like "Today, 5h ahead".
 */
data class PlaceUiModel(
    val id: Long,
    val name: String,
    val currentTime: String,
    val timeDifference: String
)