package com.example.smartalarm.feature.timer.presentation.model

enum class ShowTimerAnalyticsEvent(val eventName: String) {

    // Lifecycle Events
    SHOW_SCREEN_VIEWED("show_timer_screen_viewed"),

    // Timer Actions
    SHOW_START_TIMER("show_timer_start"),
    SHOW_PAUSE_TIMER("show_timer_pause"),
    SHOW_SNOOZE_TIMER("show_timer_snooze"),
    SHOW_RESUME_TIMER("show_timer_resume"),
    SHOW_RESTART_TIMER("show_timer_restart"),
    SHOW_STOP_TIMER("show_timer_stop");

    object Params {
        const val TIMER_ID = "timer_id"
        const val REMAINING_TIME = "remaining_time"
        const val TARGET_TIME = "target_time"
        const val TOTAL_ACTIVE_TIMERS = "total_active_timers"
    }
}