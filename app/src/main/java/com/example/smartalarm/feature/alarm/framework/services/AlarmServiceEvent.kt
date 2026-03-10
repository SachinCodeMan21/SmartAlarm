package com.example.smartalarm.feature.alarm.framework.services

// Enum class for Alarm Service events
enum class AlarmServiceEvent(val message: String) {
    ALARM_SERVICE_EVENT_TRIGGERED("Alarm service event: Triggered"),
    ALARM_SERVICE_EVENT_SNOOZED("Alarm service event: Snoozed"),
    ALARM_SERVICE_EVENT_STOPPED("Alarm service event: Stopped"),
    ALARM_SERVICE_EVENT_PAUSED("Alarm service event: Paused"),
    ALARM_SERVICE_EVENT_RESUMED("Alarm service event: Resumed")
}