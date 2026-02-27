package com.example.smartalarm.feature.clock.presentation.uiState

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.presentation.model.PlaceUiModel


/**
 * Represents the UI state of the place search screen.
 */
sealed class PlaceSearchUiState {

    /** The initial idle state before any search has been triggered. */
    object Initial : PlaceSearchUiState()

    /** Indicates that a search is currently in progress. */
    object Loading : PlaceSearchUiState()

    /**
     * Represents a successful search with a list of [PlaceUiModel] results.
     *
     * @param places The list of places ready for UI display.
     */
    data class Success(val places: List<PlaceUiModel>) : PlaceSearchUiState()

    /** Indicates that an error occurred during the search process. */
    object Error : PlaceSearchUiState()
}