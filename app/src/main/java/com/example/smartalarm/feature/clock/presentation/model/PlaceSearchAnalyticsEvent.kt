package com.example.smartalarm.feature.clock.presentation.model

enum class PlaceSearchAnalyticsEvent(val eventName: String) {

    // Navigation
    SCREEN_VIEWED("search_screen_viewed"),
    NAVIGATE_BACK_CLICKED("search_nav_back_clicked"),

    // Search Interaction
    SEARCH_QUERY_CHANGED("search_query_modified"),

    // Result Interaction
    PLACE_SELECTED_SUCCESS("search_place_selected"),
    SEARCH_NO_RESULTS("search_no_results_found");

    object Params {
        const val QUERY_LENGTH = "query_length"
        const val SELECTED_PLACE_ID = "selected_place_id"
        const val SELECTED_PLACE_NAME = "selected_place_name"
        const val RESULTS_COUNT = "results_count"
    }
}