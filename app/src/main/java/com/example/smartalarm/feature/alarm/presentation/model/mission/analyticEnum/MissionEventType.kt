package com.example.smartalarm.feature.alarm.presentation.model.mission.analyticEnum

enum class MissionEventType(val eventName: String) {

    // Math Mission Events
    START_MATH_MISSION("Start Math Mission"),
    MISSION_COMPLETED_MATH("Mission Completed Math"),
    SUBMIT_ANSWER_MATH("Submit Answer Math"),

    // Memory Mission Events
    INITIALIZE_MEMORY_MISSION("Initialize Memory Mission"),
    START_MEMORY_MISSION("Start Memory Mission"),
    SQUARE_SELECTED_MEMORY("Square Selected Memory"),

    // Shake Mission Events
    INITIALIZE_SHAKE_MISSION("Initialize Shake Mission"),
    ACCELERATION_CHANGED_SHAKE("Acceleration Changed Shake"),

    // Show Alarm Events
    LOAD_ALARM("Load Alarm"),
    LOAD_PREVIEW_ALARM("Load Preview Alarm"),
    EXIT_PREVIEW_ALARM("Exit Preview Alarm"),
    SNOOZE_ALARM("Snooze Alarm"),
    STOP_ALARM_OR_START_MISSIONS("Stop Alarm Or Start Missions"),

    // Step Mission Events
    INITIALIZE_STEP_MISSION("Initialize Step Mission"),
    STEP_DETECTED("Step Detected"),
    ACCELERATION_CHANGED_STEP("Acceleration Changed Step"),

    // Typing Mission Events
    INITIALIZE_TYPING_MISSION("Initialize Typing Mission"),
    START_TYPING_MISSION("Start Typing Mission"),
    INPUT_TEXT_CHANGED_TYPING("Input Text Changed Typing"),
    CHECK_IS_INPUT_CORRECT_TYPING("Check Is Input Correct Typing")
}