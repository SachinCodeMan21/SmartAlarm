package com.example.smartalarm.feature.clock.presentation.model

/**
 * UI model representing the clock screen state for the UI layer.
 *
 * @property formattedTime The current formatted time (e.g., "08:45 PM").
 * @property formattedDate The current formatted date (e.g., "Wed, 20 Aug").
 * @property savedPlaces List of saved places as [PlaceUiModel].
 */
data class ClockUiModel(
    val formattedTime: String = "",
    val formattedDate: String = "",
    val savedPlaces: List<PlaceUiModel> = emptyList()
)