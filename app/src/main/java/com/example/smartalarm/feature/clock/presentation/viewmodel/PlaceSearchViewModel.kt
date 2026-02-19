package com.example.smartalarm.feature.clock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.feature.clock.presentation.uiState.PlaceSearchUiState
import com.example.smartalarm.core.di.annotations.IoDispatcher
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect.*
import com.example.smartalarm.feature.clock.presentation.event.PlaceSearchEvent
import com.example.smartalarm.feature.clock.presentation.view.activity.SearchTimeZoneActivity
import java.io.IOException
import javax.inject.Inject



/**
 * ViewModel for managing the state and effects of the time zone search screen.
 *
 * Responsibilities:
 * 1. Handles search query input and fetches matching place predictions.
 * 2. Manages the selection of a place and persists it via use cases.
 * 3. Emits UI states and one-time UI effects (e.g., navigation or toasts).
 *
 * @property clockUseCases Use cases related to saving and retrieving time zone data.
 * @property placeSearchUseCases Use cases for querying places from DB or API.
 */
@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val clockUseCases: ClockUseCases,
    private val placeSearchUseCases: PlaceSearchUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel()
{

    // UI State Flow for rendering in the UI
    private val _uiState = MutableStateFlow<PlaceSearchUiState>(PlaceSearchUiState.Initial)
    val uiState: StateFlow<PlaceSearchUiState> = _uiState.asStateFlow()

    // Channel for sending one-time UI events (effects)
    private val _uiEffect = Channel<PlaceSearchEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()


    // ---------------------------------------------------------------------
    // SearchTimeZoneActivity Event Handler
    // ---------------------------------------------------------------------
    /**
     * Handles user-triggered events from the [SearchTimeZoneActivity] screen UI.
     *
     * @param event The UI event to process.
     *
     * 1. QueryChanged → initiates a place search with the entered query.
     * 2. PlaceSelected → saves the selected place to the database.
     */
    fun handleEvent(event: PlaceSearchEvent) {
        when (event) {
            is PlaceSearchEvent.NavigateBack -> postEffect(PlaceSearchEffect.Finish)
            is PlaceSearchEvent.QueryChanged -> handleQueryChange(event.query)
            is PlaceSearchEvent.PlaceSelected -> handlePlaceSelected(event.place)
            is PlaceSearchEvent.ShowSnackBarMessage -> postEffect(ShowSnackBarMessage(event.message))
        }
    }


    // ---------------------------------------------------------------------
    //  Event Handler Methods
    // ---------------------------------------------------------------------
    /**
     * Handles the logic for processing a new search query.
     *
     * 1. Sets the UI state to Loading.
     * 2. Invokes the use case to get matching place predictions.
     * 3. Updates UI state with the results.
     * 4. Emits error effects if the operation fails.
     *
     * @param query The search query entered by the user.
     */
    private fun handleQueryChange(query: String) {

        viewModelScope.launch(ioDispatcher) {

            _uiState.value = PlaceSearchUiState.Loading

            delay(1000L) // Simulate loading delay for user typing

            when (val result = placeSearchUseCases.getPlacePredictions(query)) {
                is Result.Success -> {
                    _uiState.value = PlaceSearchUiState.Success(result.data)
                }

                is Result.Error -> {
                    val message = when (result.exception) {
                        is IOException -> "No internet connection"
                        else -> result.exception.message ?: "Unknown error"
                    }
                    _uiState.value = PlaceSearchUiState.Error
                    postEffect(ShowSnackBarMessage(message))
                }
            }
        }
    }


    /**
     * Handles the selection of a place from the search result.
     *
     * 1. Triggers the use case to insert the selected place into storage.
     * 2. Emits a navigation effect to move to the Home screen on success.
     * 3. Emits an error effect if saving fails.
     *
     * @param place The selected PlaceModel to save.
     */
    private fun handlePlaceSelected(place: PlaceModel) {
        viewModelScope.launch(ioDispatcher) {
            when(clockUseCases.insertPlace(place)){
                is Result.Success -> { _uiEffect.send(NavigateToHome) }
                is Result.Error -> { postEffect(ShowSnackBarMessage("Failed to save place")) }
            }
        }
    }


    /**
     * Emits a one-time UI effect to be handled by the UI layer, such as navigation or showing a message.
     *
     * This uses a [Channel] to send [PlaceSearchEffect] events, ensuring they're only consumed once.
     *
     * @param placeSearchUIEffect The [PlaceSearchEffect] to emit.
     */
    private fun postEffect(placeSearchUIEffect : PlaceSearchEffect) = viewModelScope.launch{
        _uiEffect.send(placeSearchUIEffect)
    }



}