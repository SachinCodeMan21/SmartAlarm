package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.Mission

sealed class StepMissionEvent {


    data class InitializeMission(val mission: Mission) : StepMissionEvent()
    object StepDetected : StepMissionEvent()
    data class AccelerationChanged(val magnitude: Float) : StepMissionEvent()

}