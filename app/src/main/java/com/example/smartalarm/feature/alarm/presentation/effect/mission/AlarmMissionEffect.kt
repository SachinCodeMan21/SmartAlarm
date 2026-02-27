package com.example.smartalarm.feature.alarm.presentation.effect.mission

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission

sealed class AlarmMissionEffect {
    data class StartMissionFlow(val alarm : AlarmModel) : AlarmMissionEffect()
    data class ShowAlarmMission(val mission: Mission) : AlarmMissionEffect()

    data class ShowToastMessage(val toastMessage : String) : AlarmMissionEffect()

    data class ShowErrorMessage(val error : DataError) : AlarmMissionEffect()


    object MissionTimeout : AlarmMissionEffect()

    object MissionCompleted : AlarmMissionEffect()

    object FinishActivity : AlarmMissionEffect()

}