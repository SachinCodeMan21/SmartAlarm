package com.example.smartalarm.feature.clock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.feature.clock.presentation.uiState.PlaceSearchUiState
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect.*
import com.example.smartalarm.feature.clock.presentation.event.PlaceSearchEvent
import com.example.smartalarm.feature.clock.presentation.mapper.PlaceUiMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * ViewModel for handling place search and selection in the time zone app.
 *
 * Responsibilities include:
 *  - Handling user search queries for places.
 *  - Managing UI state and effects for the search screen.
 *  - Caching search results to avoid extra repository calls.
 *  - Inserting a selected place into the clock repository.
 *
 * @property clockUseCases Handles domain operations related to saved places in the clock.
 * @property placeSearchUseCases Handles domain operations for searching places.
 */
@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val clockUseCases: ClockUseCases,
    private val placeSearchUseCases: PlaceSearchUseCases
) : ViewModel() {

    /**
     * Current UI state for the place search screen.
     * Exposes the state as a read-only [StateFlow] for the UI.
     */
    private val _uiState = MutableStateFlow<PlaceSearchUiState>(PlaceSearchUiState.Initial)
    val uiState: StateFlow<PlaceSearchUiState> = _uiState.asStateFlow()

    /**
     * UI one-time events such as navigation or error messages.
     * Exposes the events as a [Flow] for the UI to observe.
     */
    private val _uiEffect = Channel<PlaceSearchEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    /**
     * Cache of the domain [PlaceModel] results for the current query, keyed by place ID.
     * Used to quickly retrieve the domain model when a user selects a place.
     */
    private var cachedSearchResults: Map<Long, PlaceModel> = emptyMap()

    /** Currently running search job, if any, to allow cancellation of previous queries. */
    private var searchJob: Job? = null

    // ---------------------------------------------------------------------
    // Private Helper Methods
    // ---------------------------------------------------------------------

    /**
     * Updates the UI state with the given [PlaceSearchUiState].
     *
     * @param placeSearchUiState The new state to display in the UI.
     */
    private fun updateState(placeSearchUiState: PlaceSearchUiState) {
        _uiState.value = placeSearchUiState
    }

    /**
     * Sends a one-time UI effect such as navigation or error display.
     *
     * @param placeSearchUIEffect The effect to send to the UI.
     */
    private fun postEffect(placeSearchUIEffect: PlaceSearchEffect) {
        viewModelScope.launch { _uiEffect.send(placeSearchUIEffect) }
    }

    // ---------------------------------------------------------------------
    // Event Handling
    // ---------------------------------------------------------------------

    /**
     * Handles user events from the search screen.
     *
     * @param event The [PlaceSearchEvent] triggered by the user.
     */
    fun handleEvent(event: PlaceSearchEvent) {
        when (event) {
            is PlaceSearchEvent.NavigateBack -> postEffect(NavigateToHome)
            is PlaceSearchEvent.QueryChanged -> handleQueryChange(event.query)
            is PlaceSearchEvent.PlaceSelected -> handlePlaceSelected(event.selectedPlaceId)
        }
    }

    /**
     * Handles a change in the search query.
     *
     * - Cancels any ongoing search.
     * - Performs a new search using [placeSearchUseCases].
     * - Updates the UI state with the search results or an error.
     * - Updates [cachedSearchResults] for quick access when a place is selected.
     *
     * @param query The user's search query string.
     */
    private fun handleQueryChange(query: String) {
        if (query.isEmpty()) {
            _uiState.value = PlaceSearchUiState.Initial
            cachedSearchResults = emptyMap()
            return
        }

        searchJob?.cancel()
        _uiState.value = PlaceSearchUiState.Loading

        searchJob = viewModelScope.launch {
            when (val result = placeSearchUseCases.getPlacePredictions(query)) {
                is MyResult.Success -> {
                    cachedSearchResults = result.data.associateBy { it.id }
                    val uiPlaces = PlaceUiMapper.mapToUiList(result.data)
                    updateState(PlaceSearchUiState.Success(uiPlaces))
                }
                is MyResult.Error -> {
                    updateState(PlaceSearchUiState.Error)
                    postEffect(ShowError(result.error))
                }
            }
        }
    }

    /**
     * Handles a place selection event.
     *
     * - Looks up the selected place in [cachedSearchResults] by its ID.
     * - Inserts the domain [PlaceModel] into the clock repository via [clockUseCases].
     * - Posts a navigation effect or error to the UI.
     *
     * @param selectedPlaceId The ID of the selected place.
     */
    private fun handlePlaceSelected(selectedPlaceId: Long) {
        cachedSearchResults[selectedPlaceId]?.let { domainPlace ->
            viewModelScope.launch {
                when (val result = clockUseCases.insertPlace(domainPlace)) {
                    is MyResult.Success -> postEffect(NavigateToHome)
                    is MyResult.Error -> postEffect(ShowError(result.error))
                }
            }
        }
    }
}
