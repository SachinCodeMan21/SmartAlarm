package com.example.smartalarm.feature.alarm.presentation.model.home

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.Mission
import java.time.LocalTime

data class AlarmUiModel(
    val id: Int,
    val formattedAlarmTime: String,
    val selectedDays: Set<DayOfWeek>,
    val missionIconResId: Int?,
    val alarmMissions: List<Mission?>,
    val isEnabled: Boolean
)