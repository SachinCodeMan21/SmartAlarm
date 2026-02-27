package com.example.smartalarm.feature.clock.presentation.event

import com.example.smartalarm.feature.clock.domain.model.PlaceModel


/**
 * Represents UI events triggered from the Place Search screen.
 *
 * These events are typically passed from the UI to the ViewModel
 * to perform actions like updating a query, selecting a place, or navigating.
 */
sealed class PlaceSearchEvent {

    /**
     * Event to navigate back to the previous screen.
     */
    data object NavigateBack : PlaceSearchEvent()

    /**
     * Event triggered when the user modifies the search query.
     *
     * @property query The updated query string entered by the user.
     */
    data class QueryChanged(val query: String) : PlaceSearchEvent()

    /**
     * Event triggered when the user selects a place from the search results.
     *
     * @property selectedPlaceId The selected place id that was selected.
     */
    data class PlaceSelected(val selectedPlaceId : Long) : PlaceSearchEvent()
}
