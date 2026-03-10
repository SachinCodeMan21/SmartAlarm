package com.example.smartalarm.feature.alarm.presentation.model.editor

enum class AlarmEditorViewModelAnalyticsEvent(val eventName: String) {
    // General editor events
    INITIALIZE_ALARM_EDITOR("alarm_editor_initialize"),
    ALARM_LABEL_CHANGED("alarm_editor_label_changed"),
    ALARM_TIME_CHANGED("alarm_editor_time_changed"),
    ALARM_DAILY_CHANGED("alarm_editor_is_daily_changed"),
    ALARM_DAY_TOGGLED("alarm_editor_day_toggled"),

    // Mission events
    MISSION_PLACEHOLDER_CLICKED("alarm_editor_mission_placeholder_clicked"),
    MISSION_ITEM_CLICKED("alarm_editor_mission_item_clicked"),
    MISSION_ITEM_REMOVED("alarm_editor_mission_item_removed"),
    MISSION_SELECTED("alarm_editor_mission_selected"),
    MISSION_UPDATED("alarm_editor_mission_updated"),
    MISSION_PREVIEW_STARTED("alarm_editor_mission_preview_started"),

    // Sound events
    SOUND_VOLUME_CHANGED("alarm_editor_sound_volume_changed"),
    SOUND_VIBRATION_TOGGLED("alarm_editor_sound_vibration_toggled"),
    SOUND_RINGTONE_SELECTED("alarm_editor_sound_ringtone_selected"),

    EDIT_SNOOZE("alarm_editor_edit_snooze"),
    BACK_PRESSED("alarm_editor_back_pressed"),


    // Save/Update events
    SAVE_ALARM("alarm_editor_save_alarm"),
    UPDATE_ALARM("alarm_editor_update_alarm"),
}