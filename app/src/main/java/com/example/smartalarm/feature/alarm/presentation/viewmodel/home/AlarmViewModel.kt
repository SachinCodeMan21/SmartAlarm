package com.example.smartalarm.feature.alarm.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.AlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SwipedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.ToggleAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UndoAlarmUseCase
import com.example.smartalarm.feature.alarm.presentation.effect.home.AlarmEffect
import com.example.smartalarm.feature.alarm.presentation.effect.home.AlarmEffect.*
import com.example.smartalarm.feature.alarm.presentation.event.home.AlarmEvent
import com.example.smartalarm.feature.alarm.presentation.mapper.AlarmUiMapper
import com.example.smartalarm.feature.alarm.presentation.uiState.AlarmUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing alarms within the alarm editor feature.
 *
 * This ViewModel is responsible for handling alarm-related actions, including fetching alarms, adding, editing,
 * toggling, deleting, and undoing alarm deletions. It manages the UI state and effects to provide feedback
 * to the user, such as showing loading indicators, success messages, or error messages.
 *
 * The ViewModel communicates with use cases that handle business logic for interacting with alarm data,
 * such as `GetAllAlarmsUseCase`, `SaveAlarmUseCase`, and others. It ensures that UI state is updated accordingly
 * by using `StateFlow` for the UI state and `SharedFlow` for side effects like navigation and toast messages.
 *
 * Permissions are handled before performing certain actions (e.g., scheduling alarms, posting notifications),
 * ensuring that required permissions are granted before executing the desired action.
 * @param alarmUseCase Provides use cases for managing alarms.
 * @param resourceProvider Provides resources (like strings) for error handling and messages.
 * @param alarmMapper Maps domain models to UI models for display.
 */
@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmUseCase: AlarmUseCase,
    private val resourceProvider: ResourceProvider,
    private val alarmMapper: AlarmUiMapper,
) : ViewModel()
{

    // Internal cache of domain models representing alarms.
    private val alarmCache: MutableMap<Int, AlarmModel> = mutableMapOf()

    // State flow representing the UI state of the alarm feature.
    val uiState: StateFlow<AlarmUiState> = alarmUseCase.getAllAlarmsUseCase()
        .onEach { alarms ->
            // Update your domain cache for toggle/undo etc.
            alarmCache.clear()
            alarmCache.putAll(alarms.associateBy { it.id })
        }
        .map { alarms ->
            if (alarms.isEmpty()) AlarmUiState.Empty
            else AlarmUiState.Success(alarms.map { alarmMapper.toUiModel(it) })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AlarmUiState.Loading
        )

    // Mutable shared flow for emitting UI effects such as navigation or toast messages.
    private val _uiEffect = MutableSharedFlow<AlarmEffect>(replay = 0)
    val uiEffect = _uiEffect.asSharedFlow()


    // Recently deleted alarm cache for undo
    private var recentlyDeletedAlarm: AlarmModel? = null




    // ---------------------------------------------------------------------
    // Update UI State/Effect Methods.
    // ---------------------------------------------------------------------

    /**
     * Sends a one-time UI effect to be observed by the UI layer.
     *
     * - Launches a coroutine to send the provided [effect] through the [_uiEffect] sharedFlow.
     *
     * @param effect The [AlarmEffect] to emit.
     */
    private fun postEffect(effect: AlarmEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }


    // ---------------------------------------------------------------------
    // Alarm Fragment Event Handler
    // ---------------------------------------------------------------------

    /**
     * Handles various events related to alarm management.
     *
     * This method processes different alarm events and triggers the corresponding actions,
     * such as fetching alarms, navigating to the alarm editor, toggling alarm state, or handling item swipes.
     * It ensures that the appropriate side effects (UI updates, navigation, etc.) are triggered based on the event type.
     *
     * @param event The [AlarmEvent] that represents the user or system interaction, determining the action to take.
     */
    fun handleEvent(event: AlarmEvent) {
        when (event) {
            is AlarmEvent.AddNewAlarm ->  navigateToEditAlarm()
            is AlarmEvent.ToggleAlarm -> onAlarmToggle(event)
            is AlarmEvent.UndoDeletedAlarm -> undoDelete()
            is AlarmEvent.AlarmItemSwiped -> deleteAlarm(event.deletedAlarmId)
            is AlarmEvent.AlarmItemClicked -> postEffect(NavigateToEditAlarmScreen(event.selectedAlarmId))
        }
    }




    // ---------------------------------------------------------------------
    // Alarm Fragment Events
    // ---------------------------------------------------------------------


    private fun navigateToEditAlarm() {
        postEffect(NavigateToCreateAlarmScreen)
    }


    /**
     * Handles the toggling of an alarm's enabled state.
     *
     * This method retrieves the alarm based on the provided toggledAlarmId, checks for necessary permissions,
     * and then toggles the alarm's state (enabled/disabled). If successful:
     * - If disabling the alarm and it is currently ringing, it stops the alarm service.
     * - If enabling the alarm, it shows a success toast message.
     * If an error occurs, an error toast message is displayed.
     *
     * @param event The [AlarmEvent.ToggleAlarm] containing the alarm ID and new enabled state.
     * @see ToggleAlarmUseCase
     * @see postEffect
     */
    private fun onAlarmToggle(event: AlarmEvent.ToggleAlarm) {

        val currentAlarm = getAlarm(event.toggledAlarmId) ?: return

        viewModelScope.launch {

            when (val result = alarmUseCase.toggleAlarmUseCase(currentAlarm, event.isEnabled)) {

                is MyResult.Success -> {
                    if (!event.isEnabled) {
                        if (currentAlarm.alarmState == AlarmState.RINGING) {
                            postEffect(StopAlarmService)
                        }
                    } else {
                        postEffect(ShowToastMessage(result.data))
                    }
                }

                is MyResult.Error -> {
                    postEffect(ShowError(result.error))
                }

            }
        }
    }


    /**
     * Deletes an alarm based on its ID and handles the result of the deletion process.
     *
     * This method retrieves the alarm to be deleted from the cache using the provided [deletedAlarmId].
     * If the alarm exists, it proceeds with the deletion using [SwipedAlarmUseCase].
     * On success, a snackBar message is shown to confirm the action.
     * If an error occurs, an error state is updated.
     *
     * @param deletedAlarmId The ID of the alarm to be deleted.
     * @see SwipedAlarmUseCase
     * @see postEffect
     */
    private fun deleteAlarm(deletedAlarmId: Int) {

        // Retrieve the alarm to be deleted from the cache
        recentlyDeletedAlarm = getAlarm(deletedAlarmId) ?: return

        // If the alarm exists, proceed with the deletion and related actions
        recentlyDeletedAlarm?.let {

            viewModelScope.launch {

                when (val result = alarmUseCase.swipedAlarmUseCase(it.id, it.alarmState)) {

                    is MyResult.Success -> {
                        postEffect(ShowSnackBarMessage(swipedAlarmId = deletedAlarmId))
                    }

                    is MyResult.Error -> {
                        postEffect(ShowError(result.error))
                    }
                }

            }
        }
    }


    /**
     * Undoes the deletion of an alarm, restoring it to the upcoming state.
     *
     * This method attempts to restore a recently deleted alarm by copying its details and resetting its state to `UPCOMING`.
     * It then calls [UndoAlarmUseCase] to perform the undo action. If successful, a toast message is shown, and the
     * recently deleted alarm is cleared. In case of an error, a toast message with the error is displayed.
     *
     * @see UndoAlarmUseCase
     * @see postEffect
     */
    private fun undoDelete() {

        val undoAlarm = recentlyDeletedAlarm?.copy(id = 0, alarmState = AlarmState.UPCOMING) ?: return

        viewModelScope.launch {

            when (val result = alarmUseCase.undoAlarmUseCase(undoAlarm)) {
                is MyResult.Success -> {
                    postEffect(ShowToastMessage(resourceProvider.getString(R.string.undo_successful)))
                    recentlyDeletedAlarm = null
                }

                is MyResult.Error -> {
                    postEffect(ShowError(result.error))
                }
            }

        }

    }




    // ---------------------------------------------------------------------
    // Runtime Permission Helper Method
    // ---------------------------------------------------------------------

    /**
     * Retrieves an alarm from the internal cache by its ID.
     *
     * @param alarmId The unique identifier of the alarm to retrieve.
     * @return The [AlarmModel] if found, or `null` if not present.
     */
    private fun getAlarm(alarmId: Int): AlarmModel? {
        return alarmCache[alarmId]
    }

}