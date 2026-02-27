package com.example.smartalarm.feature.alarm.presentation.model.mission

data class ShowAlarmUiModel(
    val id : Int = 0,
    val formattedAlarmTime: String = "",
    val alarmLabel: String = "",
    val snoozeCount: Int = 3,
    val isMissionAvailable: Boolean = false
)
