package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.Mission

sealed class TypingMissionEvent {

    data class InitializeMission(val mission: Mission) : TypingMissionEvent()

    object StartMission : TypingMissionEvent()
    data class InputTextChanged(val input: String) : TypingMissionEvent()
    data class CheckIsInputCorrect(val input: String) : TypingMissionEvent()
}