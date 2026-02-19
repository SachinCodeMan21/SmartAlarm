package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel

sealed class ShowAlarmEvent {
    data class LoadAlarm(val alarmId : Int) : ShowAlarmEvent()
    data class LoadPreview(val previewAlarm : AlarmModel) : ShowAlarmEvent()
    object ExitPreview : ShowAlarmEvent()
    object SnoozeAlarm : ShowAlarmEvent()
    object StopAlarmOrStartMissions : ShowAlarmEvent()
}
