package com.example.smartalarm.feature.clock.domain.model


/**
 * Represents the UI state of the clock screen.
 *
 * Responsibilities:
 * 1. Holds the current formatted time for display.
 * 2. Holds the current formatted date for display.
 * 3. Contains a list of user-saved places (e.g., time zones ) to display.
 *
 * This data class is immutable and designed to be a presentation model
 * that drives the UI rendering.
 */
data class ClockModel(
    val formattedTime: String = "",
    val formattedDate: String = "",
    val savedPlaces: List<PlaceModel> = emptyList()
)

