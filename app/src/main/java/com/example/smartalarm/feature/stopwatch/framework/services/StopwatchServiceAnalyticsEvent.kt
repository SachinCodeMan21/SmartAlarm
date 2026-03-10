package com.example.smartalarm.feature.stopwatch.framework.services

enum class StopwatchServiceAnalyticsEvent(val eventName: String) {
    // Lifecycle Events
    START_FOREGROUND_SERVICE("stopwatch_service_start_foreground"),
    PAUSE_STOPWATCH("stopwatch_service_pause"),
    RESUME_STOPWATCH("stopwatch_service_resume"),
    RESET_STOPWATCH("stopwatch_service_reset"),
    LAP_STOPWATCH("stopwatch_service_lap"),
    STOP_FOREGROUND_SERVICE("stopwatch_service_stop_foreground"),

    // Error Events
    ERROR_STOPWATCH_SERVICE("stopwatch_service_error")
}