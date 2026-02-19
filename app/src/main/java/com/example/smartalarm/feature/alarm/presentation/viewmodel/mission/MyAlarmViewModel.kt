package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.presentation.effect.mission.AlarmMissionEffect
import com.example.smartalarm.feature.alarm.presentation.effect.mission.AlarmMissionEffect.*
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.job.MissionCountDownJobManager
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class MyAlarmViewModel @Inject constructor(
    private val updateAlarmUseCase: UpdateAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val alarmTimeHelper: AlarmTimeHelper,
    private val missionCountDownJobManager: MissionCountDownJobManager,
    private val alarmServiceController: AlarmServiceController,
    private val alarmRingtonePlayer: AlarmRingtoneManager,
    private val vibrationManager: VibrationManager,
    private val numberFormatter: NumberFormatter,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel()
{

    companion object{

        // Default mission timeout duration set to 1 minute (60,000 milliseconds)
        private const val MISSION_TIMEOUT_TIME = 60000L

        // Typing mission timeout duration set to 3 minutes (180,000 milliseconds)
        private const val TYPING_MISSION_TIMEOUT_TIME = 3 * 60000L

    }

    private val _timerProgressState = MutableStateFlow(100)
    val timerProgressState = _timerProgressState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AlarmMissionEffect>(0)
    val uiEffect = _uiEffect.asSharedFlow()

    private var currentAlarm: AlarmModel? = null
    private var currentMissionIndex = 0
    private var isCurrentMissionCompleted = false


    // Preview Focused Variable
    private var isPreview = false


    // -------------------------------------------------------------------
    // Setter
    // -------------------------------------------------------------------
    fun setPreview(isPreview : Boolean){
        this.isPreview = isPreview
    }


    // -------------------------------------------------------------------
    // Update UI State & Effect Methods
    // -------------------------------------------------------------------

    /**
     * Posts an effect to trigger UI changes in the view layer.
     * This method emits the given effect to the UI effect flow, which is observed by the UI to respond accordingly.
     *
     * @param effect The effect to be posted, representing a change or action to be reflected in the UI.
     */
    private fun postEffect(effect: AlarmMissionEffect){
        viewModelScope.launch{ _uiEffect.emit(effect) }
    }





    // -------------------------------------------------------------------
    // Handling Alarm Mission Activity Events
    // -------------------------------------------------------------------

    /**
     * Handles various shared events related to alarm missions.
     * Based on the event type, it triggers the appropriate actions such as
     * starting the mission flow, completing the mission, handling timeouts,
     * or finishing the mission activity.
     */
    fun handleSharedEvent(event: AlarmMissionEvent){
        when(event){
            is AlarmMissionEvent.StartMissionFlow -> startMissionFlow(event.alarmModel)
            is AlarmMissionEvent.MissionCompleted -> handleMissionCompleted()
            is AlarmMissionEvent.MissionFailedTimeout -> handleMissionTimeout()
            is AlarmMissionEvent.FinishMissionActivity -> postEffect(FinishActivity)
        }
    }






    // ----------------------------------------------------------------------
    // Alarm Mission Event Handler
    // ----------------------------------------------------------------------

    /**
     * Initiates the mission flow for the specified alarm by starting the first mission in the sequence.
     * It updates the current alarm state and begins the first mission.
     */
    private fun startMissionFlow(alarm: AlarmModel){
        currentAlarm = alarm
        startNextMission()
    }


    /**
     * Starts the next mission in the sequence.
     * It retrieves the current mission from the alarm, sets up the timeout timer,
     * and posts an effect to show the mission. If no mission exists, it finishes the activity.
     */
    private fun startNextMission() {
        currentAlarm?.missions?.getOrNull(currentMissionIndex)?.let { mission ->
            isCurrentMissionCompleted = false
            val missionTimeoutDuration = if (mission.type == MissionType.Typing) TYPING_MISSION_TIMEOUT_TIME else MISSION_TIMEOUT_TIME
            startMissionTimeoutTimer(missionTimeoutDuration)
            postEffect(ShowAlarmMission(mission))
        } ?: finishMissionActivity()
    }


    /**
     * Handles the completion of the current mission in the flow.
     * - Increments the mission index and checks if there are more missions to complete.
     * - If more missions remain, it starts the next mission.
     * - If no more missions remain, it either finishes the mission activity (for previews)
     *   or processes the completion of all missions (for non-previews).
     */
    private fun handleMissionCompleted() {

        currentAlarm?.let {

            isCurrentMissionCompleted = true
            stopMissionTimeoutTimer()

            currentMissionIndex++

            // More missions left
            if (currentMissionIndex < it.missions.size) {
                startNextMission()
            }
            else {
                if (isPreview) finishMissionActivity()  else handleAllMissionsCompleted(it)
            }
        } ?: finishMissionActivity()
    }


    /**
     * Handles the completion of all missions in the flow.
     * Updates the alarm and schedules the next one if it is a repeating alarm.
     */
    private fun handleAllMissionsCompleted(alarm: AlarmModel) {

        val isRepeatingAlarm = alarm.days.isNotEmpty()
        val snoozeSettings = alarm.snoozeSettings.copy(
            isAlarmSnoozed = false,
            snoozedCount = alarm.snoozeSettings.snoozeLimit
        )

        val missionCompletedAlarm = alarm.copy(
            isEnabled = isRepeatingAlarm,
            snoozeSettings = snoozeSettings,
            alarmState = AlarmState.STOPPED
        )

        updateAlarm(missionCompletedAlarm) {
            currentAlarm = missionCompletedAlarm
            alarmScheduler.cancelSmartAlarmTimeout(missionCompletedAlarm.id)
            if (isRepeatingAlarm) {
                scheduleNextAlarm(missionCompletedAlarm)
            }
            finishMissionActivity()
        }
    }


    private fun handleMissionTimeout() {
        stopMissionTimeoutTimer()
        currentAlarm?.let {
            alarmServiceController.resumeAlarm(it.id)
            postEffect(MissionTimeout)
            if (isPreview){
                alarmRingtonePlayer.playAlarmRingtone(it.alarmSound,it.volume)
                if (it.isVibrateEnabled) vibrationManager.startVibration()
            }
        }
    }





    // ---------------------------------------------------------------------
    // Timer Control
    // ---------------------------------------------------------------------

    /**
     * Starts a countdown timer for the mission with the specified duration.
     * Updates the mission progress as the timer ticks, and triggers the mission timeout handler once the timer finishes.
     *
     * @param timeoutDurationInMillis The duration for the mission timeout in milliseconds.
     */
    private fun startMissionTimeoutTimer(timeoutDurationInMillis: Long) {

        _timerProgressState.value = 100

        missionCountDownJobManager.startCountdown(
            scope = viewModelScope,
            targetDuration = timeoutDurationInMillis,
            onTick = { progress ->
                _timerProgressState.value = progress
            },
            onFinish = {
                _timerProgressState.value = 0
                if (!isCurrentMissionCompleted) {
                    handleMissionTimeout()
                }
            }
        )
    }

    /**
     * Stops the current mission countdown timer and resets the progress state to 100%.
     * This is typically used when the mission is completed or manually stopped.
     *
     * @see MissionCountDownJobManager.stopCountdown
     */
    private fun stopMissionTimeoutTimer() {
        missionCountDownJobManager.stopCountdown()
        _timerProgressState.value = 100
    }


    /**
     * Stops the alarm service and triggers the effect to finish the mission activity.
     * This method is used to finalize the alarm mission flow once the mission is completed or aborted.
     *
     * @see AlarmServiceController.stopAlarmService
     * @see postEffect
     */
    private fun finishMissionActivity() {
        currentAlarm?.let {
            alarmServiceController.stopAlarmService()
            postEffect(FinishActivity)
        }
    }




    // ----------------------------------------------------------------------
    // Helper Methods
    // ----------------------------------------------------------------------

    /**
     * Updates the specified alarm and triggers a callback on success.
     * If the update operation fails, an error message is shown to the user.
     *
     * @param alarm The alarm model to be updated.
     * @param onSuccess The callback function to be executed upon a successful update.
     */
    private fun updateAlarm(alarm: AlarmModel, onSuccess: () -> Unit) = viewModelScope.launch(dispatcher) {
        when (val result = updateAlarmUseCase(alarm)) {
            is Result.Success -> {
                onSuccess()
            }
            is Result.Error -> {
                postEffect(ShowToastMessage(result.exception.message.toString()))
            }
        }
    }


    /**
     * Schedules the next alarm trigger time if the alarm is set to repeat.
     */
    private fun scheduleNextAlarm(alarm: AlarmModel) {
        val nextAlarm = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)
        alarmScheduler.scheduleSmartAlarm(alarm.id, nextAlarm)
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


