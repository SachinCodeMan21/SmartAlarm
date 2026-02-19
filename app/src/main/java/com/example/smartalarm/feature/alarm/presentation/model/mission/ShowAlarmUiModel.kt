package com.example.smartalarm.feature.alarm.presentation.model.mission

import java.time.LocalTime

data class ShowAlarmUiModel(
    val id : Int = 0,
    val alarmTime: LocalTime = LocalTime.now(),
    val snoozeCount: Int = 3,
    val isMissionAvailable: Boolean = false
)
