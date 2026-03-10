package com.example.smartalarm.feature.stopwatch.presentation.model

enum class StopwatchAnalyticsEvent(val eventName: String) {
    // Lifecycle Events
    SCREEN_VIEWED("stopwatch_screen_viewed"),

    // Interaction Events
    START_STOPWATCH("stopwatch_start"),
    PAUSE_STOPWATCH("stopwatch_pause"),
    RESET_STOPWATCH("stopwatch_reset"),
    LAP_STOPWATCH("stopwatch_lap"),

    // Service Events
    START_FOREGROUND_SERVICE("stopwatch_start_foreground_service"),
    STOP_FOREGROUND_SERVICE("stopwatch_stop_foreground_service");

}