package com.example.smartalarm.core.framework.analytics

interface AnalyticsHelper {
    fun logEvent(name: String, vararg params: Pair<String, Any>)
    fun setUserProperty(name: String, value: String)
    fun logScreenView(screenName: String, className: String)
}