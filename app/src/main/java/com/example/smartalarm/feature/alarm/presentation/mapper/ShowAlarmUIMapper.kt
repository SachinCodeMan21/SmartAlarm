package com.example.smartalarm.feature.alarm.presentation.mapper

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShowAlarmUiModel

object ShowAlarmUIMapper {

    fun toUiModel(alarm: AlarmModel): ShowAlarmUiModel {
        return ShowAlarmUiModel(
            id = alarm.id,
            alarmTime = alarm.time,
            snoozeCount = alarm.snoozeSettings.snoozedCount,
            isMissionAvailable = alarm.missions.any { !it.isCompleted }
        )
    }
}
