package com.example.smartalarm.feature.clock.presentation.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.di.annotations.IoDispatcher
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.ClockModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.presentation.effect.ClockEffect
import com.example.smartalarm.feature.clock.presentation.event.ClockEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import kotlinx.coroutines.flow.update


@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clockUseCases: ClockUseCases,
    private val clockUpdater: ClockUpdaterJob,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiModel = MutableStateFlow(ClockModel())
    val uiModel: StateFlow<ClockModel> = _uiModel

    private val _uiEffect = Channel<ClockEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ClockEffect> = _uiEffect.receiveAsFlow()

    fun onEvent(event: ClockEvent) {
        when (event) {
            is ClockEvent.LoadSelectedTimeZones -> loadTimeZones()
            is ClockEvent.StopClockUiUpdates -> clockUpdater.stopClockUpdaterJob()
            is ClockEvent.DeleteTimeZone -> deleteTimeZone(event.deletedTimeZone)
            is ClockEvent.UndoDeletedTimeZone -> undoDeletedTimeZone(event.deletedTimeZone)
            is ClockEvent.AddNewTimeZone -> postEffect(ClockEffect.NavigateToAddTimeZoneActivity)
            is ClockEvent.ShowToastMessage -> postEffect(ClockEffect.ShowToast(event.message))
        }
    }

    private fun loadTimeZones() {
        viewModelScope.launch(ioDispatcher) {
            when (val result = clockUseCases.getAllSavedPlaces()) {
                is MyResult.Success -> startClockUpdates(result.data)
                is MyResult.Error -> {
                    // Handle specific DataError types here if needed
                    postEffect(ClockEffect.ShowToast("Failed to load time zones"))
                }
            }
        }
    }

    private fun startClockUpdates(savedPlaces: List<PlaceModel>) {
        clockUpdater.startClockUpdaterJob(
            scope = viewModelScope,
            savedPlaces = savedPlaces,
            onUpdate = { updatedPlaces, time, date ->
                _uiModel.value = ClockModel(
                    formattedTime = time,
                    formattedDate = date,
                    savedPlaces = updatedPlaces
                )
            },
            onError = { error ->
                postEffect(ClockEffect.ShowToast("Update Error: ${error.localizedMessage}"))
            }
        )
    }

    private fun deleteTimeZone(deletedTimeZone: PlaceModel) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = clockUseCases.deletePlaceById(deletedTimeZone.id)) {
                is MyResult.Success -> {
                    val updatedList = _uiModel.value.savedPlaces.filterNot { it.id == deletedTimeZone.id }
                    _uiModel.update { it.copy(savedPlaces = updatedList) }
                    postEffect(ClockEffect.DeleteTimeZone(deletedTimeZone))
                }
                is MyResult.Error -> {
                    postEffect(ClockEffect.ShowToast("Could not delete item"))
                }
            }
        }
    }

    private fun undoDeletedTimeZone(deletedTimeZone: PlaceModel) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = clockUseCases.insertPlace(deletedTimeZone)) {
                is MyResult.Success -> {
                    val updatedList = _uiModel.value.savedPlaces + deletedTimeZone
                    _uiModel.update { it.copy(savedPlaces = updatedList) }
                    postEffect(ClockEffect.ShowToast("Undo successful"))
                }
                is MyResult.Error -> {
                    postEffect(ClockEffect.ShowToast("Failed to restore time zone"))
                }
            }
        }
    }

    private fun postEffect(effect: ClockEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }
}

/**
 * ViewModel responsible for managing the clock UI state and handling user events.
 *
 * This ViewModel:
 * - Loads and observes saved time zones
 * - Starts and stops a periodic time update loop
 * - Handles user actions such as deleting, undoing deletion, or navigation
 * - Emits one-time UI effects (e.g., navigation or toast messages)
 *
 * @param clockUseCases Use cases for interacting with time zone data.
 * @param ioDispatcher Coroutine dispatcher for background work.
 */
//@HiltViewModel
//class ClockViewModel @Inject constructor(
//    private val clockUseCases: ClockUseCases,
//    private val clockUpdater: ClockUpdaterJob,
//    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
//) : ViewModel()
//{
//
//    /**
//     * Internal mutable state that holds the current UI model for the clock screen.
//     */
//    private val _uiModel = MutableStateFlow(ClockModel())
//
//    /**
//     * Public [StateFlow] that exposes the current clock UI state to the UI layer.
//     */
//    val uiModel: StateFlow<ClockModel> = _uiModel
//
//    /**
//     * Internal channel used to emit one-time UI effects such as navigation or toasts.
//     *
//     * These are not part of the persistent UI state and are consumed only once.
//     */
//    private val _uiEffect = Channel<ClockEffect>(Channel.BUFFERED)
//
//    /**
//     * Public [Flow] that the UI collects to receive one-time side effects.
//     */
//    val uiEffect: Flow<ClockEffect> = _uiEffect.receiveAsFlow()
//
//
//
//    // ---------------------------------------------------------------------
//    // Public Clock UI Event Handling
//    // ---------------------------------------------------------------------
//
//    /**
//     * Handles UI events and dispatches the appropriate actions or effects.
//     */
//    fun onEvent(event: ClockEvent) {
//        when (event) {
//            is ClockEvent.LoadSelectedTimeZones -> loadTimeZones()
//            is ClockEvent.StopClockUiUpdates -> clockUpdater.stopClockUpdaterJob()
//            is ClockEvent.DeleteTimeZone -> deleteTimeZone(event.deletedTimeZone)
//            is ClockEvent.UndoDeletedTimeZone -> undoDeletedTimeZone(event.deletedTimeZone)
//            is ClockEvent.AddNewTimeZone -> postEffect(ClockEffect.NavigateToAddTimeZoneActivity)
//            is ClockEvent.ShowToastMessage -> postEffect(ClockEffect.ShowToast(event.message))
//        }
//    }
//
//    // ---------------------------------------------------------------------
//    // Data Loading & Undo Actions
//    // ---------------------------------------------------------------------
//
//    /**
//     * Loads all saved time zones asynchronously and starts the time update loop.
//     *
//     * Emits a toast effect if loading fails.
//     */
//    private fun loadTimeZones() {
//        viewModelScope.launch(ioDispatcher) {
//            when (val result = clockUseCases.getAllSavedPlaces()) {
//                is Result.Success -> startClockUpdates(result.data)
//                is Result.Error -> {}//postEffect(ClockEffect.ShowToast("Error: ${result.exception.localizedMessage}"))
//            }
//        }
//    }
//
//
//    /**
//     * Starts a repeating background job that updates the clock UI every minute.
//     *
//     * @param savedPlaces The list of time zones to display and update.
//     */
//    private fun startClockUpdates(savedPlaces: List<PlaceModel>) {
//        clockUpdater.startClockUpdaterJob(
//            scope = viewModelScope,
//            savedPlaces = savedPlaces,
//            onUpdate = { updatedPlaces, time, date ->
//                _uiModel.value = ClockModel(
//                    formattedTime = time,
//                    formattedDate = date,
//                    savedPlaces = updatedPlaces
//                )
//            },
//            onError = { error ->
//                postEffect(ClockEffect.ShowToast("Error: ${error.localizedMessage}"))
//            }
//        )
//    }
//
//
//    /**
//     * Deletes a time zone (place) from the data source and updates the UI state.
//     *
//     * This function:
//     * - Calls the use case to delete the specified [PlaceModel] by its ID.
//     * - If successful, removes the time zone from the current UI state list.
//     * - Emits a [ClockEffect.DeleteTimeZone] to inform the UI.
//     * - Emits a toast message if the deletion fails.
//     *
//     * @param deletedTimeZone The [PlaceModel] representing the time zone to be deleted.
//     */
//    private fun deleteTimeZone(deletedTimeZone: PlaceModel) {
//        viewModelScope.launch(ioDispatcher) {
//            when (val result = clockUseCases.deletePlaceById(deletedTimeZone.id)) {
//                is Result.Success -> {
//                    val updatedList = _uiModel.value.savedPlaces.filterNot { it.id == deletedTimeZone.id }
//                    _uiModel.emit(_uiModel.value.copy(savedPlaces = updatedList))
//                    postEffect(ClockEffect.DeleteTimeZone(deletedTimeZone))
//                }
//
//                is Result.Error -> {
//                    //postEffect(ClockEffect.ShowToast("Delete failed: ${result.exception.localizedMessage ?: "Unknown error"}"))
//                }
//            }
//        }
//    }
//
//
//    /**
//     * Re-inserts a previously deleted time zone (place) into the data source and updates the UI state.
//     *
//     * This function:
//     * - Calls the use case to insert the given [PlaceModel] back into the database.
//     * - If successful, appends the restored time zone to the current UI state's saved places list.
//     * - Emits a toast message to indicate success or failure.
//     *
//     * @param deletedTimeZone The [PlaceModel] representing the time zone to be restored.
//     */
//    private fun undoDeletedTimeZone(deletedTimeZone: PlaceModel) {
//        viewModelScope.launch(ioDispatcher) {
//            when (val result = clockUseCases.insertPlace(deletedTimeZone)) {
//                is Result.Success -> {
//                    val updatedList = _uiModel.value.savedPlaces + deletedTimeZone
//                    _uiModel.emit(_uiModel.value.copy(savedPlaces = updatedList))
//                    postEffect(ClockEffect.ShowToast("Undo successful"))
//                }
//
//                is Result.Error -> {} //postEffect( ClockEffect.ShowToast("Undo failed: ${result.exception.localizedMessage ?: "Unknown error"}") )
//            }
//        }
//    }
//
//
//    /**
//     * Sends a one-time UI effect, such as a toast or navigation event.
//     *
//     * @param effect The [ClockEffect] to emit.
//     */
//    private fun postEffect(effect: ClockEffect) {
//        viewModelScope.launch(ioDispatcher) {
//            _uiEffect.send(effect)
//        }
//    }
//
//}