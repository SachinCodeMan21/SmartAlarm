package com.example.smartalarm.feature.alarm.presentation.viewmodel.editor


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.framework.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.framework.permission.PermissionManager
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.PostSaveOrUpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.presentation.effect.editor.AlarmEditorEffect
import com.example.smartalarm.feature.alarm.presentation.effect.editor.AlarmEditorEffect.*
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorSystemEvent
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.mapper.AlarmUiMapper
import com.example.smartalarm.feature.alarm.presentation.model.editor.AlarmEditorHomeUiModel
import com.example.smartalarm.feature.alarm.presentation.view.statemanager.contract.AlarmEditorHomeStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the alarm editor state and handling user/system events.
 *
 * ## Responsibilities:
 * - Manages alarm data, including creation, updating, and persistence of alarm settings.
 * - Handles permissions for features like notifications and exact alarms.
 * - Exposes UI state as a [StateFlow] and UI effects as a [SharedFlow] for the UI layer to observe.
 * - Processes system events (e.g., permission grants, snooze updates) and user events (e.g., label changes, sound selection).
 * - Coordinates navigation to different screens and displays UI effects like loading indicators, error messages, and success messages.
 *
 * ## Key Features:
 * - Supports alarm creation and updates, including label, time, recurrence, missions, and sound settings.
 * - Handles permission management for notifications, full-screen intents, and exact alarm scheduling.
 * - Emits UI effects for navigation, error handling, and loading indicators.
 *
 * @param getAlarmByIdUseCase Use case for fetching alarm data by ID.
 * @param saveAlarmUseCase Use case for saving a new alarm.
 * @param updateAlarmUseCase Use case for updating an existing alarm.
 * @param postSaveOrUpdateAlarmUseCase Use case for posting the result of a save or update operation.
 * @param alarmEditorStateManager State manager for the alarm editor's internal state.
 * @param alarmUiMapper Mapper for transforming alarm domain models into UI models.
 * @param permissionManager Permission manager to check and request necessary permissions.
 * @param numberFormatter Utility to format numbers (e.g., for time display).
 * @param DefaultDispatcher Coroutine dispatcher for background tasks.
 */
@HiltViewModel
class AlarmEditorViewModel @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val postSaveOrUpdateAlarmUseCase: PostSaveOrUpdateAlarmUseCase,
    private val alarmEditorStateManager: AlarmEditorHomeStateManager,
    private val alarmUiMapper: AlarmUiMapper,
    private val permissionManager: PermissionManager,
    private val numberFormatter: NumberFormatter,
) : ViewModel()
{

    companion object {
        // Delay (in milliseconds) for the save/update alarm progress loader animation.
        const val SAVE_UPDATE_ALARM_PROGRESS_DELAY = 500L
    }

    // Public UI state derived from domain state
    val uiState: StateFlow<AlarmEditorHomeUiModel> = alarmEditorStateManager.getAlarmState
        .map {
            try {
                alarmUiMapper.toEditorHomeUiModel(it)  // Safe mapping with error handling
            } catch (_: Exception) {
                alarmUiMapper.toEditorHomeUiModel(AlarmModel()) // Return a safe fallback state
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = alarmUiMapper.toEditorHomeUiModel(AlarmModel())
        )


    private val _uiEffect = MutableSharedFlow<AlarmEditorEffect>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEffect: SharedFlow<AlarmEditorEffect> = _uiEffect.asSharedFlow()




    // ---------------------------------------------------------------------
    //  UI Effect Update Methods
    // ---------------------------------------------------------------------

    /**
     * Emits a one-time UI effect to be observed by the UI layer.
     *
     * This function uses a coroutine to send the given [effect] through the [_uiEffect] channel,
     * allowing the UI to react to transient events such as navigation, dialogs, or snackBars.
     *
     * @param effect The [AlarmEditorEffect] to emit.
     */
    private fun postEffect(effect: AlarmEditorEffect) {
        val result = _uiEffect.tryEmit(effect)  // synchronous, no coroutine needed
        Log.d("TAG", "postEffect tryEmit $effect result=$result")
    }


    // ---------------------------------------------------------------------
    // Editor Home Fragment System & User Event Dispatcher Methods
    // ---------------------------------------------------------------------

    fun handleSystemEvent(event: AlarmEditorSystemEvent) {
        when (event) {
            is AlarmEditorSystemEvent.InitializeAlarmEditorState -> initEditorHomeAlarm(event.existingAlarmId)
            is AlarmEditorSystemEvent.SnoozeUpdated -> alarmEditorStateManager.updateSnooze(event.snoozeSettings)
        }
    }

    fun handleUserEvent(event: AlarmEditorUserEvent) {
        when (event) {
            is AlarmEditorUserEvent.AlarmEvent -> handleAlarmEvent(event)
            is AlarmEditorUserEvent.MissionEvent -> handleMissionEvent(event)
            is AlarmEditorUserEvent.SoundEvent -> handleSoundEvent(event)
            is AlarmEditorUserEvent.ActionEvent -> handleActionEvent(event)
            is AlarmEditorUserEvent.NavigationEvent -> handleNavigationEvent(event)
        }
    }



    // ---------------------------------------------------------------------
    // Editor Home Fragment User Event Sub Dispatcher Methods
    // ---------------------------------------------------------------------
    private fun handleAlarmEvent(event: AlarmEditorUserEvent.AlarmEvent) {
        with(alarmEditorStateManager) {
            when (event) {
                is AlarmEditorUserEvent.AlarmEvent.LabelChanged -> updateLabel(event.label)
                is AlarmEditorUserEvent.AlarmEvent.TimeChanged -> updateTime(event.hour, event.minute, event.amPm)
                is AlarmEditorUserEvent.AlarmEvent.IsDailyChanged -> updateIsDaily(event.isDaily)
                is AlarmEditorUserEvent.AlarmEvent.DayToggled -> toggleDay(event.dayIndex)
            }
        }
    }
    private fun handleMissionEvent(event: AlarmEditorUserEvent.MissionEvent) {
        val state = alarmEditorStateManager.getAlarmState.value

        when (event) {
            is AlarmEditorUserEvent.MissionEvent.PlaceholderClicked ->
                postEffect(
                    ShowMissionPickerBottomSheet(
                        position = event.position,
                        existingMission = null,
                        usedMissions = state.missions
                    )
                )

            is AlarmEditorUserEvent.MissionEvent.ItemClicked ->
                postEffect(
                    ShowMissionPickerBottomSheet(
                        position = event.position,
                        existingMission = event.mission,
                        usedMissions = state.missions.filterIndexed { i, _ -> i != event.position }
                    )
                )

            is AlarmEditorUserEvent.MissionEvent.RemoveClicked ->
                alarmEditorStateManager.removeMissionAt(event.position)

            is AlarmEditorUserEvent.MissionEvent.Selected ->
                postEffect(ShowSelectedMissionBottomSheet(event.position, event.mission))

            is AlarmEditorUserEvent.MissionEvent.Updated ->
                alarmEditorStateManager.updateMission(event.position, event.mission)

            is AlarmEditorUserEvent.MissionEvent.Preview -> startAlarmMissionPreview(event.mission)
        }
    }
    private fun handleSoundEvent(event: AlarmEditorUserEvent.SoundEvent) {
        val state = alarmEditorStateManager.getAlarmState.value

        when (event) {
            is AlarmEditorUserEvent.SoundEvent.VolumeChanged -> alarmEditorStateManager.updateVolume(event.volume)

            is AlarmEditorUserEvent.SoundEvent.VibrationToggled -> alarmEditorStateManager.updateVibration(event.enabled)

            AlarmEditorUserEvent.SoundEvent.LaunchPicker -> postEffect(LaunchAlarmSoundPicker(state.alarmSound))

            is AlarmEditorUserEvent.SoundEvent.RingtoneSelected -> alarmEditorStateManager.updateRingtone(event.uri)
        }
    }
    private fun handleActionEvent(event: AlarmEditorUserEvent.ActionEvent) {
        val state = alarmEditorStateManager.getAlarmState.value

        when (event) {
            AlarmEditorUserEvent.ActionEvent.EditSnooze ->
                postEffect(NavigateToSnoozeAlarmFragment(state.snoozeSettings))

            AlarmEditorUserEvent.ActionEvent.SaveOrUpdate ->
                //postEffect(ShowSaveUpdateLoadingIndicator(true))
                saveOrUpdateAlarm()
        }
    }
    private fun handleNavigationEvent(event: AlarmEditorUserEvent.NavigationEvent) {
        when (event) {
            AlarmEditorUserEvent.NavigationEvent.HandleCustomBackNavigation -> postEffect(FinishEditorActivity)
        }
    }



    // ---------------------------------------------------------------------
    // Editor Home Fragment Event Handler Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the alarm editor state, loading existing alarm data if an ID is provided.
     *
     * This method ensures that the alarm editor is properly set up, either with a fresh state or by loading
     * an existing alarm based on the provided `existingAlarmId`. It handles both success and failure scenarios
     * when fetching the alarm data, ensuring the UI reflects the correct state.
     */
    private fun initEditorHomeAlarm(existingAlarmId: Int) {

        if (existingAlarmId == 0) {
            alarmEditorStateManager.initAlarmState()
            return
        }

        viewModelScope.launch {
            when (val result = getAlarmByIdUseCase(existingAlarmId)) {
                is MyResult.Success -> alarmEditorStateManager.setAlarm(result.data)
                is MyResult.Error -> postEffect(ShowError(result.error))
            }
        }
    }

    /**
     * Saves a new alarm or updates an existing alarm based on its current state.
     *
     * This function performs the following steps:
     * 1. Retrieves the current alarm state from [alarmEditorStateManager].
     * 2. Shows a loading indicator during the save/update process.
     * 3. Delays briefly to allow the progress UI to display ([SAVE_UPDATE_ALARM_PROGRESS_DELAY]).
     * 4. If the alarm is new (`id == 0`), it calls [saveAlarmUseCase]; otherwise, it calls [updateAlarmUseCase].
     * 5. Handles the result:
     *    - On success, delegates post-processing to [handlePostSaveOrUpdateAlarm].
     *    - On error, hides the loading indicator and posts an error message via [postEffect].
     *
     * @sideEffect Posts UI effects such as:
     *  - [ShowSaveUpdateLoadingIndicator]
     *  - [ShowError]
     *  - [ShowToastMessage] and [FinishEditorActivity] via [handlePostSaveOrUpdateAlarm]
     */
    private fun saveOrUpdateAlarm() = viewModelScope.launch {
        val alarm = alarmEditorStateManager.getAlarmState.value

        postEffect(ShowSaveUpdateLoadingIndicator(true))
        delay(SAVE_UPDATE_ALARM_PROGRESS_DELAY)

        if (alarm.id == 0) {
            when (val result = saveAlarmUseCase(alarm)) {
                is MyResult.Success -> handlePostSaveOrUpdateAlarm(alarm.copy(id = result.data))
                is MyResult.Error -> {
                    postEffect(ShowSaveUpdateLoadingIndicator(false))
                    postEffect(ShowError(result.error))
                }
            }
        } else {
            when (val result = updateAlarmUseCase(alarm.copy(isEnabled = true))) {
                is MyResult.Success -> handlePostSaveOrUpdateAlarm(alarm)
                is MyResult.Error -> {
                    postEffect(ShowSaveUpdateLoadingIndicator(false))
                    postEffect(ShowError(result.error))
                }
            }
        }
    }


    /**
     * Handles post-processing after saving or updating an alarm.
     *
     * This method performs the following actions after a save or update operation on an alarm:
     * 1. Hides the loading indicator.
     * 2. If the operation was successful, shows a success message and finishes the editor activity.
     * 3. If an error occurs, shows an error message to the user.
     *
     * @param alarm The [AlarmModel] that was saved or updated.
     *
     * @sideEffect Posts UI effects such as:
     *  - [ShowSaveUpdateLoadingIndicator]
     *  - [ShowToastMessage]
     *  - [FinishEditorActivity]
     *  - [ShowError]
     */
    private fun handlePostSaveOrUpdateAlarm(alarm: AlarmModel) {
        postEffect(ShowSaveUpdateLoadingIndicator(false))
        when (val result = postSaveOrUpdateAlarmUseCase(alarm)) {
            is MyResult.Success -> {
                postEffect(ShowToastMessage(result.data))
                postEffect(FinishEditorActivity)
            }
            is MyResult.Error -> {
                postEffect(ShowSaveUpdateLoadingIndicator(false))
                postEffect(ShowError(result.error))
            }
        }
    }


    /**
     * Initiates a preview of the alarm with the provided mission.
     *
     * This method creates a copy of the current alarm state, replacing the missions list
     * with the provided `previewMission`. It then triggers a navigation effect to an
     * activity designed to preview the alarm with this specific mission.
     *
     * @param previewMission The mission to be previewed with the alarm.
     */
    private fun startAlarmMissionPreview(previewMission: Mission) {
        val previewAlarmModel = alarmEditorStateManager.getAlarmState.value.copy(missions = listOf(previewMission))
        postEffect(NavigateToAlarmActivityForMissionPreview(previewAlarmModel))
    }




    // ---------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------

    /**
     * Converts an integer into a localized string representation.
     *
     * This method formats the given number according to the user's locale.
     * It allows an optional parameter to control whether the number should display a leading zero.
     *
     * @param number The integer to be formatted.
     * @param leadingZero A flag indicating whether a leading zero should be included for single-digit numbers. Default is `true`.
     * @return A localized string representation of the number.
     */
    fun getLocalizedNumber(number: Int, leadingZero: Boolean = true): String {
        return numberFormatter.formatLocalizedNumber(number.toLong(), leadingZero)
    }

}