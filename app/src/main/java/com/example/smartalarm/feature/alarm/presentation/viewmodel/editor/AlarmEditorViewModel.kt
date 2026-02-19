package com.example.smartalarm.feature.alarm.presentation.viewmodel.editor


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.permission.PermissionManager
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
import kotlinx.coroutines.CoroutineDispatcher
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
    @param:DefaultDispatcher val defaultDispatcher: CoroutineDispatcher
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


    // Private SharedFlow for emitting UI effects
    private val _uiEffect = MutableSharedFlow<AlarmEditorEffect>(replay = 0)

    // Exposes a public flow for collecting effects
    val uiEffect = _uiEffect.asSharedFlow()



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
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    // ---------------------------------------------------------------------
    // Editor Home Fragment System Event Handler
    // ---------------------------------------------------------------------

    /**
     * Handles system events related to alarm editing.
     *
     * This method processes different system events (e.g., initialization, permission grants, snooze updates)
     * and triggers the appropriate actions, ensuring the alarm editor state is updated accordingly.
     * It streamlines the handling of various events to ensure a smooth user experience and state consistency.
     */
    fun handleSystemEvent(event: AlarmEditorSystemEvent) {
        when (event) {
            is AlarmEditorSystemEvent.InitializeAlarmEditorState -> initEditorHomeAlarm(event.existingAlarmId)
            is AlarmEditorSystemEvent.ExactAlarmPermissionGranted,
            is AlarmEditorSystemEvent.PostNotificationPermissionGranted,
            is AlarmEditorSystemEvent.RetryPendingSaveAction -> handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)
            is AlarmEditorSystemEvent.SnoozeUpdated -> alarmEditorStateManager.updateSnooze(event.snoozeSettings)
        }
    }


    /**
     * Initializes the alarm editor state, loading existing alarm data if an ID is provided.
     *
     * This method ensures that the alarm editor is properly set up, either with a fresh state or by loading
     * an existing alarm based on the provided `existingAlarmId`. It handles both success and failure scenarios
     * when fetching the alarm data, ensuring the UI reflects the correct state.
     */
    private fun initEditorHomeAlarm(existingAlarmId: Int) {
        if (existingAlarmId != 0) {
            viewModelScope.launch {
                when (val result = getAlarmByIdUseCase(existingAlarmId)) {
                    is Result.Success -> alarmEditorStateManager.setAlarm(result.data)
                    is Result.Error ->  postEffect(ShowToastMessage(result.exception.message.toString()))
                }
            }
        }
        else{
            alarmEditorStateManager.initAlarmState()
        }
    }


    // ---------------------------------------------------------------------
    // Editor Home Fragment User Event Handler
    // ---------------------------------------------------------------------

    /**
     * Handles various user events triggered in the alarm editor.
     *
     * This method processes different types of user events, such as changing the alarm label,
     * adjusting the alarm time, toggling days of the week, managing alarm missions, and
     * configuring sound and vibration settings. Each event results in updating the alarm state
     * or triggering a UI effect (e.g., showing a bottom sheet, navigating to another screen).
     *
     * @param event The user event that triggered this method, which can be of various types
     *              such as label changes, mission handling, sound adjustments, or navigation actions.
     */
    fun handleUserEvent(event: AlarmEditorUserEvent) {
        when (event) {

            // Represents events related to editing the alarm label, time, daily recurrence, and toggling days of the week.
            is AlarmEditorUserEvent.LabelChanged -> alarmEditorStateManager.updateLabel(event.label)
            is AlarmEditorUserEvent.TimeChanged -> alarmEditorStateManager.updateTime(event.hour, event.minute, event.amPm)
            is AlarmEditorUserEvent.IsDailyChanged -> alarmEditorStateManager.updateIsDaily(event.isDaily)
            is AlarmEditorUserEvent.DayToggled -> alarmEditorStateManager.toggleDay(event.toggleDayIndex)

            // Represents events related to interacting with alarm missions (e.g., handling clicks on mission items).
            is AlarmEditorUserEvent.HandleMissionItemPlaceHolderClick -> {
                postEffect(
                    ShowMissionPickerBottomSheet(
                        position = event.position,
                        existingMission = null,
                        usedMissions = alarmEditorStateManager.getAlarmState.value.missions
                    )
                )
            }
            is AlarmEditorUserEvent.HandleMissionItemClick -> {
                postEffect(
                    ShowMissionPickerBottomSheet(
                        position = event.position,
                        existingMission = event.existingMission,
                        usedMissions = alarmEditorStateManager.getAlarmState.value.missions.filterIndexed { index, _ -> index != event.position }
                    )
                )
            }
            is AlarmEditorUserEvent.HandleRemoveMissionClick -> alarmEditorStateManager.removeMissionAt(event.position)

            // Represents events for [ showing dummy mission preview ,  selecting or updating an alarm mission ].
            is AlarmEditorUserEvent.AlarmMissionSelected -> {
                postEffect(ShowSelectedMissionBottomSheet(event.position, event.selectedMission))
            }
            is AlarmEditorUserEvent.StartAlarmMissionPreview -> startAlarmMissionPreview(event.previewMission)
            is AlarmEditorUserEvent.UpdateAlarmMission -> alarmEditorStateManager.updateMission(event.position, event.mission)

            // Represents events related to alarm sound settings (volume and vibration), and selecting ringtones.
            is AlarmEditorUserEvent.VolumeChanged -> alarmEditorStateManager.updateVolume(event.volume)
            is AlarmEditorUserEvent.VibrationToggled ->  alarmEditorStateManager.updateVibration(event.isEnabled)

            is AlarmEditorUserEvent.LaunchAlarmSoundPicker -> {
                postEffect(LaunchAlarmSoundPicker(alarmEditorStateManager.getAlarmState.value.alarmSound))
            }
            is AlarmEditorUserEvent.RingtoneSelected -> alarmEditorStateManager.updateRingtone(event.uri)

            // Represents navigation to snooze screen ,  saving / updating  the alarm.
            is AlarmEditorUserEvent.EditSnoozeClick -> {
                postEffect(NavigateToSnoozeAlarmFragment(alarmEditorStateManager.getAlarmState.value.snoozeSettings))
            }
            is AlarmEditorUserEvent.SaveOrUpdateAlarmClick -> saveOrUpdateAlarm()

            // Represents back click events in the alarm editor.
            is AlarmEditorUserEvent.OnSystemBackPressed,
            is AlarmEditorUserEvent.OnToolbarBackPressed -> {
                postEffect(FinishEditorActivity)
            }
        }
    }


    /**
     * Saves or updates the current alarm based on its state.
     *
     * This method checks the necessary permissions before proceeding with saving or updating the alarm.
     * It handles both new and existing alarms, showing a loading indicator during the process and
     * handling success or error results. After the operation, it triggers appropriate UI updates or error messages.
     */
    private fun saveOrUpdateAlarm() {


        checkPermissionsAndExecute {

            val alarm = alarmEditorStateManager.getAlarmState.value

            viewModelScope.launch {

                postEffect(ShowSaveUpdateLoadingIndicator(true))
                delay(SAVE_UPDATE_ALARM_PROGRESS_DELAY)

                if (alarm.id == 0) {
                    when (val result = saveAlarmUseCase(alarm)) {
                        is Result.Success -> {
                            handlePostSaveOrUpdateAlarm(alarm.copy(id = result.data))
                        }

                        is Result.Error -> {
                            postEffect(ShowSaveUpdateLoadingIndicator(false))
                            postEffect(ShowError(result.exception.message.toString()))
                        }
                    }
                }
                else {
                    when (val result = updateAlarmUseCase(alarm.copy(isEnabled = true))) {
                        is Result.Success -> {
                            handlePostSaveOrUpdateAlarm(alarm)
                        }

                        is Result.Error -> {
                            postEffect(ShowSaveUpdateLoadingIndicator(false))
                            postEffect(ShowError(result.exception.message.toString()))
                        }
                    }
                }


            }
        }
    }


    /**
     * Handles the post events after saving or updating an alarm.
     *
     * This method handles post events after a save or update an alarm. It hides the loading indicator,
     * shows a success message (if the operation was successful), and finishes the activity. If an error occurs,
     * it displays an error message to the user.
     */
    private fun handlePostSaveOrUpdateAlarm(alarm: AlarmModel) {

        postEffect(ShowSaveUpdateLoadingIndicator(false))

        when (val result = postSaveOrUpdateAlarmUseCase(alarm)) {

            is Result.Success -> {
                postEffect(ShowToastMessage(result.data))
                postEffect(FinishEditorActivity)

            }

            is Result.Error -> {
                postEffect(ShowError(result.exception.message.toString()))
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
     * Checks for necessary permissions before executing a given action.
     *
     * This method ensures that the required permissions for posting notifications, displaying fullscreen notifications,
     * and scheduling exact alarms are granted before executing the provided action. If any permission is missing,
     * it triggers the appropriate permission request.
     *
     * @param onAllPermissionGranted A callback to execute when all necessary permissions are granted.
     */
    private fun checkPermissionsAndExecute(onAllPermissionGranted: () -> Unit) {
        when {
            !permissionManager.isPostNotificationPermissionGranted() -> postEffect(
                LaunchPostNotificationPermissionRequest
            )

            !permissionManager.isFullScreenNotificationPermissionGranted() -> postEffect(
                LaunchFullScreenNotificationPermissionRequest
            )

            !permissionManager.isScheduleExactAlarmPermissionGranted() -> postEffect(
                LaunchExactAlarmPermissionRequest
            )

            else -> onAllPermissionGranted()
        }
    }


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