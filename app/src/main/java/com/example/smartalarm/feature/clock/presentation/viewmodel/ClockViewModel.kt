package com.example.smartalarm.feature.clock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.presentation.effect.ClockEffect
import com.example.smartalarm.feature.clock.presentation.event.ClockEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.UpdateClockUseCase
import com.example.smartalarm.feature.clock.presentation.mapper.PlaceUiMapper
import com.example.smartalarm.feature.clock.presentation.model.ClockUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clockUseCases: ClockUseCases,
    private val systemClockHelper: SystemClockHelper,
    private val timeFormatter: TimeFormatter,
    private val updateClockUseCase: UpdateClockUseCase,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val _uiModel = MutableStateFlow(ClockUiModel())
    val uiModel: StateFlow<ClockUiModel> = _uiModel

    private val _uiEffect = Channel<ClockEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ClockEffect> = _uiEffect.receiveAsFlow()

    private var cachedPlacesMap: Map<Long, PlaceModel> = emptyMap()
    private var deletedPlace: PlaceModel? = null
    private var updateJob: Job? = null



    // ---------------------------------------------------------------------
    // UI Effects
    // ---------------------------------------------------------------------
    private fun postEffect(effect: ClockEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
    }



    // ---------------------------------------------------------------------
    // Event Delegate
    // ---------------------------------------------------------------------
    fun onEvent(event: ClockEvent) {
        when (event) {
            is ClockEvent.LoadSelectedTimeZones -> loadTimeZones()
            is ClockEvent.StopClockUiUpdates -> stopClockUpdater()
            is ClockEvent.DeleteTimeZone -> deleteTimeZone(event.deletedTimeZoneId)
            is ClockEvent.UndoDeletedTimeZone -> undoDeletedTimeZone()
            is ClockEvent.AddNewTimeZone -> postEffect(ClockEffect.NavigateToAddTimeZoneScreen)
            is ClockEvent.ShowToastMessage -> postEffect(ClockEffect.ShowToast(event.message))
        }
    }


    // ---------------------------------------------------------------------
    // Load and Update Time Zones
    // ---------------------------------------------------------------------
    private fun loadTimeZones() {
        viewModelScope.launch {
            when (val result = clockUseCases.getAllSavedPlaces()) {
                is MyResult.Success -> {
                    cachedPlacesMap = result.data.associateBy { it.id }
                    startClockUpdater(result.data)
                }
                is MyResult.Error -> postEffect(ClockEffect.ShowToast("Failed to load time zones"))
            }
        }
    }

    private fun startClockUpdater(savedPlaces: List<PlaceModel>) {

        stopClockUpdater() // cancel existing job

        updateJob = viewModelScope.launch {

            while (isActive) {

                val now = systemClockHelper.getCurrentTime()
                val formattedTime = timeFormatter.formatClockTime(now)
                val formattedDate = timeFormatter.formatDayMonth(now)

                val updatedPlaces = updateClockUseCase(savedPlaces)
                cachedPlacesMap = updatedPlaces.associateBy { it.id }

                val savedPlacesUiModelList = PlaceUiMapper.mapToUiList(updatedPlaces)
                _uiModel.value = ClockUiModel(
                    formattedTime = formattedTime,
                    formattedDate = formattedDate,
                    savedPlaces = savedPlacesUiModelList
                )

                val delayMillis = TimeUnit.MINUTES.toMillis(1) - (now % TimeUnit.MINUTES.toMillis(1))
                delay(delayMillis)
            }
        }
    }

    private fun stopClockUpdater() {
        updateJob?.cancel()
        updateJob = null
    }




    // ---------------------------------------------------------------------
    // Delete / Undo
    // ---------------------------------------------------------------------

    private fun deleteTimeZone(deletedPlaceId: Long) {

        val placeToDelete = cachedPlacesMap[deletedPlaceId] ?: return // get from cache

        viewModelScope.launch {
            when (clockUseCases.deletePlaceById(deletedPlaceId)) {

                is MyResult.Success -> {
                    // Cache the deleted place for undo
                    deletedPlace = placeToDelete

                    // Remove from cache map
                    cachedPlacesMap = cachedPlacesMap - deletedPlaceId

                    // Remove from UI model
                    val updatedList = _uiModel.value.savedPlaces.filterNot { it.id == deletedPlaceId }
                    _uiModel.update { it.copy(savedPlaces = updatedList) }

                    postEffect(ClockEffect.DeleteTimeZone(placeToDelete))
                }

                is MyResult.Error -> postEffect(ClockEffect.ShowToast(resourceProvider.getString(R.string.could_not_delete_item)))
            }
        }
    }

    private fun undoDeletedTimeZone() {

        val place = deletedPlace ?: return // Nothing to undo

        viewModelScope.launch {

            when (clockUseCases.insertPlace(place)) {
                is MyResult.Success -> {

                    // Restore in cache map
                    cachedPlacesMap = cachedPlacesMap + (place.id to place)

                    // Restore in UI model
                    val updatedList = PlaceUiMapper.mapToUiList(cachedPlacesMap.values.toList())
                    _uiModel.update { it.copy(savedPlaces = updatedList) }

                    postEffect(ClockEffect.ShowToast(resourceProvider.getString(R.string.undo_timezone_successful)))

                    deletedPlace = null

                }

                is MyResult.Error -> postEffect(ClockEffect.ShowToast(resourceProvider.getString(R.string.failed_to_restore_time_zone)))
            }
        }
    }

}

