package com.example.smartalarm.feature.alarm.presentation.mapper

import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShowAlarmUiModel
import javax.inject.Inject

class ShowAlarmUIMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
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