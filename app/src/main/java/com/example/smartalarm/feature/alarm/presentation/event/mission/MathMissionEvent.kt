package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.Mission

sealed class MathMissionEvent {
    data class StartMission(val mission: Mission) : MathMissionEvent()
    object MissionCompleted : MathMissionEvent()
    data class SubmitAnswer(val ans: String) : MathMissionEvent()
}