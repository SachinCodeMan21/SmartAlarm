package com.example.smartalarm.feature.timer.presentation.model

enum class TimerAnalyticsEvent(val eventName: String) {

    // Lifecycle Events
    SCREEN_VIEWED("timer_screen_viewed"),

    // Interaction Events
    NEW_TIMER_CREATED("timer_created"),
    TIMER_DELETE_CLICKED("timer_deleted"),
    TIMER_KEYPAD_CLICKED("timer_keypad_clicked"),
    TIMER_INIT_UI("timer_init_ui");

    object Params {
        const val TIMER_DURATION_MS = "timer_duration_ms"
        const val BUTTON_LABEL = "button_label"
        const val TOTAL_ACTIVE_TIMERS = "total_active_timers"
    }

}