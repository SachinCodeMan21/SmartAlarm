package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.Mission


sealed class MemoryMissionEvent {

    data class InitializeMission(val mission : Mission) : MemoryMissionEvent()

    object StartMission : MemoryMissionEvent()

    data class SquareSelected(val index : Int) : MemoryMissionEvent()

}