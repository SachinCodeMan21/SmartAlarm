package com.example.smartalarm.feature.alarm.presentation.model.editor

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.MissionItem

data class AlarmEditorHomeUiModel(
    val label: String?,
    val hour: Int,
    val minute: Int,
    val amPm: Int,
    val isDailyAlarm: Boolean,
    val selectedDays: Set<DayOfWeek>,
    val missionItemList: List<MissionItem>, // includes both data & placeholder
    val formattedMissionSlotText: String,
    val volume: Int,
    val isVibrateEnabled: Boolean,
    val alarmSoundTitle: String,
    val formattedSnoozedText : String
)