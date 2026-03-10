package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.analytics.ErrorLogger
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SnoozeAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.StopAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.effect.mission.ShowAlarmEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShowAlarmEvent
import com.example.smartalarm.feature.alarm.presentation.mapper.ShowAlarmUIMapper
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShowAlarmUiModel
import com.example.smartalarm.feature.alarm.presentation.model.mission.analyticEnum.MissionEventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for handling the logic of the alarm screen.
 *
 * This ViewModel manages the UI state and side effects related to showing an alarm, such as:
 * - Loading alarm data and updating the UI.
 * - Handling events like snooze, stop, and starting missions.
 * - Managing alarm preview and exiting preview modes.
 * - Emitting UI effects for navigation and showing toast messages.
 *
 * It uses various use cases and services to interact with the data layer and control the alarm service.
 *
 * @param getAlarmByIdUseCase Use case for fetching alarm data by ID.
 * @param snoozeAlarmUseCase Use case for snoozing the alarm.
 * @param stopAlarmUseCase Use case for stopping the alarm.
 * @param alarmServiceController Controller for managing alarm services.
 * @param alarmRingtoneManager Manager for playing and stopping alarm sounds.
 */
@HiltViewModel
class ShowAlarmViewModel @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val snoozeAlarmUseCase: SnoozeAlarmUseCase,
    private val stopAlarmUseCase: StopAlarmUseCase,
    private val alarmServiceController: AlarmServiceController,
    private val alarmRingtoneManager: AlarmRingtoneManager,
    private val vibrationManager: VibrationManager,
    private val showAlarmUIMapper: ShowAlarmUIMapper,
    private val resourceProvider: ResourceProvider,
    private val analyticsHelper: AnalyticsHelper,
    private val errorLogger: ErrorLogger
) : ViewModel()
{

    // UI State and Effects
    private val _uiState = MutableStateFlow(ShowAlarmUiModel())
    val uiState: StateFlow<ShowAlarmUiModel> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ShowAlarmEffect>(0)
    val uiEffect = _uiEffect.asSharedFlow()

    private var currentAlarm: AlarmModel? = null
    private var isPreview : Boolean = false


    // -------------------------------------------------------------------
    // Update UI State & Effects
    // -------------------------------------------------------------------

    /**
     * Updates the UI state with the provided alarm model.
     *
     * @param alarm The alarm data used to update the UI.
     */
    private fun updateState(alarm: AlarmModel) {
        val uiModel = showAlarmUIMapper.toUiModel(alarm)
        currentAlarm = alarm
        _uiState.value = uiModel
    }

    /**
     * Emits a UI effect to trigger side effects like navigation or showing messages.
     *
     * @param effect The effect to be emitted.
     */
    private fun postEffect(effect: ShowAlarmEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    // -------------------------------------------------------------------
    // Handling ShowAlarm Mission Activity Events
    // -------------------------------------------------------------------

    /**
     * Handles the incoming events for the alarm screen.
     *
     * @param event The event to be handled.
     */
    fun handleEvent(event: ShowAlarmEvent) {
        when (event) {
            is ShowAlarmEvent.LoadAlarm -> {
                loadAlarm(event.alarmId)
                analyticsHelper.logEvent(MissionEventType.LOAD_ALARM.eventName)
            }
            is ShowAlarmEvent.LoadPreview -> {
                loadPreview(event.previewAlarm)
                analyticsHelper.logEvent(MissionEventType.LOAD_PREVIEW_ALARM.eventName)
            }
            is ShowAlarmEvent.SnoozeAlarm -> {
                snoozeAlarm()
                analyticsHelper.logEvent(MissionEventType.SNOOZE_ALARM.eventName)
            }
            is ShowAlarmEvent.StopAlarmOrStartMissions -> {
                stopAlarmOrStartMissions()
                analyticsHelper.logEvent(MissionEventType.STOP_ALARM_OR_START_MISSIONS.eventName)
            }
            is ShowAlarmEvent.ExitPreview -> {
                handleExitPreview()
                analyticsHelper.logEvent(MissionEventType.EXIT_PREVIEW_ALARM.eventName)
            }
        }
    }

    // -------------------------------------------------------------------
    // Alarm Loading, Snooze & Stop Event Handlers
    // -------------------------------------------------------------------

    /**
     * Loads the alarm details using the [alarmId], but only if the ID is valid.
     * This check prevents unnecessary database queries or API calls for invalid IDs.
     */
    private fun loadAlarm(alarmId: Int) {

        if (alarmId <= 0) return

        viewModelScope.launch {
            when (val result = getAlarmByIdUseCase(alarmId)) {
                is MyResult.Success -> {
                    if (result.data == null){
                        postEffect(ShowAlarmEffect.ShowError(resourceProvider.getString(R.string.failed_this_alarm_no_longer_exists)))
                        return@launch
                    }
                    updateState(result.data)
                }
                is MyResult.Error -> {
                    logAlarmError(result, "LoadAlarm_ID_${alarmId}")
                    postEffect(ShowAlarmEffect.ShowError(resourceProvider.getString(R.string.failed_to_load_alarm)))
                }
            }
        }
    }

    /**
     * Snoozes the active alarm if present. The check ensures the operation only happens when there's an active alarm.
     */
    private fun snoozeAlarm() {
        val alarm = currentAlarm ?: return

        viewModelScope.launch {
            when (val result = snoozeAlarmUseCase(alarm)) {
                is MyResult.Success -> {}
                is MyResult.Error -> {
                    logAlarmError(result, "SnoozeAlarm_ID_${alarm.id}")
                    postEffect(ShowAlarmEffect.ShowError(resourceProvider.getString(R.string.error_snooze_alarm)))
                }
            }
        }
    }

    /**
     * Stops the alarm or starts the mission flow based on whether missions exist.
     * The check prevents starting unnecessary missions if none are available.
     */
    private fun stopAlarmOrStartMissions() {
        val alarm = currentAlarm ?: return

        viewModelScope.launch {
            if (alarm.missions.isEmpty()) {

                when (val result = stopAlarmUseCase(alarm)) {
                    is MyResult.Success -> {}
                    is MyResult.Error -> {
                        logAlarmError(result, "StopAlarm_ID_${alarm.id}")
                        postEffect(ShowAlarmEffect.ShowError(resourceProvider.getString(R.string.error_stop_alarm)))
                    }
                }
            } else {
                if (isPreview) {
                    alarmRingtoneManager.stopAlarmRingtone()
                } else {
                    alarmServiceController.pauseAlarm(alarm.id)
                }
                postEffect(ShowAlarmEffect.StartMissionFlow(alarm))
            }
        }
    }




    // -------------------------------------------------------------------
    // Alarm Preview & Exit Event Handlers
    // -------------------------------------------------------------------

    /**
     * Loads the alarm preview data and starts playing the alarm ringtone.
     *
     * @param previewAlarmModel The alarm data for the preview mode.
     */
    private fun loadPreview(previewAlarmModel: AlarmModel) {
        isPreview = true
        updateState(previewAlarmModel)
        alarmRingtoneManager.playAlarmRingtone(previewAlarmModel.alarmSound, previewAlarmModel.volume)
    }

    /**
     * Stops the alarm ringtone and finishes the activity when exiting preview mode.
     */
    private fun handleExitPreview() {
        alarmRingtoneManager.stopAlarmRingtone()
        vibrationManager.stopVibration()
        postEffect(ShowAlarmEffect.FinishActivity)
    }



    /**
     * Called when the ViewModel is about to be destroyed.
     *
     * Ensures that the alarm ringtone is stopped to prevent it from playing
     * after the ViewModel is cleared and the associated UI is no longer visible.
     */
    override fun onCleared() {
        super.onCleared()
        alarmRingtoneManager.stopAlarmRingtone()
    }

    private fun <T> logAlarmError(result: MyResult<T, DataError>, actionTag: String) {
        if (result is MyResult.Error) {
            val error = result.error
            val throwable = if (error is DataError.Unexpected) {
                error.throwable
            } else {
                // Grouping by "AlarmError" helps distinguish from Timer/Clock issues
                Exception("ShowAlarmViewmodelError [$actionTag]: $error")
            }
            errorLogger.recordException(throwable)
        }
    }
}
