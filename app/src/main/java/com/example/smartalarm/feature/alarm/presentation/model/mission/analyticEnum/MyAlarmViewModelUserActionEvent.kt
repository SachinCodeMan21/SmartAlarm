package com.example.smartalarm.feature.alarm.presentation.model.mission.analyticEnum

enum class MyAlarmViewModelUserActionEvent(val eventName: String) {
    START_MISSION_FLOW("user_start_mission_flow"),
    MISSION_COMPLETED("user_mission_completed"),
    MISSION_FAILED_TIMEOUT("user_mission_failed_timeout"),
    FINISH_MISSION_ACTIVITY("user_finish_mission_activity")
}