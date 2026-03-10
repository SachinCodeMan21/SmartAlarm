package com.example.smartalarm.feature.clock.presentation.model

enum class ClockAnalyticsEvent(val eventName: String) {

    // Lifecycle Events
    SCREEN_VIEWED("clock_screen_viewed"),
    NAVIGATE_TO_TIMEZONE_SEARCH("clock_nav_to_search"),

    // Interaction Events
    ADD_TIMEZONE_CLICKED("clock_add_timezone_clicked"),
    TIMEZONE_SWIPE_DELETED("clock_timezone_deleted"),
    TIMEZONE_UNDO_CLICKED("clock_timezone_undo_clicked");

    object Params {
        const val TIMEZONE_ID = "timezone_id"
        const val TIMEZONE_NAME = "timezone_name"
        const val TOTAL_SAVED_COUNT = "total_saved_count"
        const val ACTION_TAG = "action_tag"
    }
}