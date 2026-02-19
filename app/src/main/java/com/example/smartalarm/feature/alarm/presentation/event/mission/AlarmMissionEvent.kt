package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel

sealed class AlarmMissionEvent {
    data class StartMissionFlow(val alarmModel : AlarmModel) : AlarmMissionEvent()
    object MissionFailedTimeout : AlarmMissionEvent()

    object MissionCompleted : AlarmMissionEvent()
    object FinishMissionActivity : AlarmMissionEvent()

}