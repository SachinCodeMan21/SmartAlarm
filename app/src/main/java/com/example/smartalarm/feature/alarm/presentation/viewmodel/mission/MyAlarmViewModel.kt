package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

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
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAllAlarmsUseCase
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


@HiltViewModel
class MyAlarmViewModel @Inject constructor(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
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

    private val _uiEffect = Channel<AlarmMissionEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()


    private var currentAlarm: AlarmModel? = null
    private var currentMissionIndex = 0
    private var isCurrentMissionCompleted = false
    private var isPreview = false

    private var observingAlarmId = -1



    fun observeAlarmState(alarmId: Int) {
        if (observingAlarmId == alarmId) return
        observingAlarmId = alarmId

        viewModelScope.launch {
            getAllAlarmsUseCase().collect { alarms ->
                val thisAlarm = alarms.firstOrNull { it.id == alarmId }
                when {
                    thisAlarm?.alarmState == AlarmState.RINGING -> {
                        currentAlarm = thisAlarm
                    }
                    currentAlarm != null -> {
                        // Was ringing, now stopped/snoozed externally → close activity
                        postEffect(FinishActivity)
                    }
                }
            }
        }
    }


    // -------------------------------------------------------------------
    // Setter
    // -------------------------------------------------------------------
    fun setPreview(isPreview : Boolean){
        this.isPreview = isPreview
    }


    // -------------------------------------------------------------------
    // Update UI State & Effect Methods
    // -------------------------------------------------------------------

    private fun postEffect(effect: AlarmMissionEffect){
        viewModelScope.launch{ _uiEffect.send(effect) }
    }





    // -------------------------------------------------------------------
    // Handling Alarm Mission Activity Events
    // -------------------------------------------------------------------
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

    private fun startMissionFlow(alarm: AlarmModel){
        currentAlarm = alarm
        startNextMission()
    }
    private fun startNextMission() {
        currentAlarm?.missions?.getOrNull(currentMissionIndex)?.let { mission ->
            isCurrentMissionCompleted = false
            val missionTimeoutDuration = if (mission.type == MissionType.Typing) TYPING_MISSION_TIMEOUT_TIME else MISSION_TIMEOUT_TIME
            startMissionTimeoutTimer(missionTimeoutDuration)
            postEffect(ShowAlarmMission(mission))
        } ?: finishMissionActivity()
    }
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
            currentMissionIndex = 0  // ✅ reset so mission restarts fresh next time
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

    private fun stopMissionTimeoutTimer() {
        missionCountDownJobManager.stopCountdown()
        _timerProgressState.value = 100
    }

    private fun finishMissionActivity() {
        currentAlarm?.let {
            //alarmServiceController.stopAlarmService()
            //postEffect(FinishActivity)
            if (isPreview) postEffect(FinishActivity)
        }
    }




    // ----------------------------------------------------------------------
    // Helper Methods
    // ----------------------------------------------------------------------

    private fun updateAlarm(alarm: AlarmModel, onSuccess: () -> Unit) = viewModelScope.launch(dispatcher) {
        when (val result = updateAlarmUseCase(alarm)) {
            is Result.Success -> {
                onSuccess()
            }
            is Result.Error -> {
                //postEffect(ShowToastMessage(result.exception.message.toString()))
            }
        }
    }
    private fun scheduleNextAlarm(alarm: AlarmModel) {
        val nextAlarm = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)
        alarmScheduler.scheduleSmartAlarm(alarm.id, nextAlarm)
    }

    fun getLocalizedNumber(number: Int, leadingZero: Boolean = true): String {
        return numberFormatter.formatLocalizedNumber(number.toLong(), leadingZero)
    }

}


