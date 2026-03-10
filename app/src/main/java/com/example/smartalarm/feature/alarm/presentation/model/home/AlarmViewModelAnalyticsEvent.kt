package com.example.smartalarm.feature.alarm.presentation.model.home

enum class AlarmViewModelAnalyticsEvent(val eventName: String) {
    // Events related to alarm management
    ADD_NEW_ALARM("alarm_add_new_alarm"),
    TOGGLE_ALARM("alarm_toggle"),
    UNDO_DELETED_ALARM("alarm_undo_deleted"),
    DELETE_ALARM("alarm_delete"),
    NAVIGATE_TO_EDIT_ALARM("alarm_navigate_to_edit"),

    // Error handling events
    ERROR_ALARM_TOGGLE("alarm_toggle_error"),
    ERROR_ALARM_DELETE("alarm_delete_error"),
    ERROR_ALARM_UNDO("alarm_undo_delete_error")
}