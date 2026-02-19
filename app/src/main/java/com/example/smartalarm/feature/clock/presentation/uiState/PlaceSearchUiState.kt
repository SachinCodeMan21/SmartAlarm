package com.example.smartalarm.feature.clock.presentation.uiState

import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Represents the different UI states for the place search screen.
 *
 * Used by the UI layer to render appropriate content based on the current state of the place search operation.
 */
sealed class PlaceSearchUiState {

    /**
     * The initial idle state before any search has been triggered.
     */
    object Initial : PlaceSearchUiState()

    /**
     * Indicates that a search is currently in progress.
     */
    object Loading : PlaceSearchUiState()

    /**
     * Represents a successful search with a list of [PlaceModel] results.
     *
     * @param places The list of places returned from the search.
     */
    data class Success(val places: List<PlaceModel>) : PlaceSearchUiState()

    /**
     * Indicates that an error occurred during the search process.
     */
    object Error : PlaceSearchUiState()
}
