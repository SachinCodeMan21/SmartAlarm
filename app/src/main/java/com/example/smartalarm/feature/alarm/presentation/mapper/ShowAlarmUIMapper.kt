package com.example.smartalarm.feature.alarm.presentation.mapper

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShowAlarmUiModel
import com.example.smartalarm.feature.alarm.utility.formatter.AlarmTimeFormatter
import javax.inject.Inject

class ShowAlarmUIMapper @Inject constructor(
    private val timeFormatter: AlarmTimeFormatter
) {

    fun toUiModel(alarm: AlarmModel): ShowAlarmUiModel {
        return ShowAlarmUiModel(
            id = alarm.id,
            formattedAlarmTime = timeFormatter.formatToAlarmTime(alarm.time.hour,alarm.time.minute),
            alarmLabel = alarm.label,
            snoozeCount = alarm.snoozeSettings.snoozedCount,
            isMissionAvailable = alarm.missions.any { !it.isCompleted }
        )
    }

}