package com.example.smartalarm.feature.alarm.framework.controller.contract


interface AlarmServiceController {
    fun startAlarm(alarmId:Int)
    fun snoozeAlarm(alarmId : Int)
    fun pauseAlarm(alarmId: Int)
    fun resumeAlarm(alarmId: Int)
    fun stopAlarm(alarmId: Int)
    fun stopAlarmService()
}