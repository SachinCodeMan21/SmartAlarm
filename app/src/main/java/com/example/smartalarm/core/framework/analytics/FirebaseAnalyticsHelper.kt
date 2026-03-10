package com.example.smartalarm.core.framework.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun logEvent(name: String, vararg params: Pair<String, Any>) {
        firebaseAnalytics.logEvent(name, params.toBundle())
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override fun logScreenView(screenName: String, className: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // Extension to convert varargs to Bundle
    private fun Array<out Pair<String, Any>>.toBundle(): Bundle? {
        if (isEmpty()) return null
        return Bundle().apply {
            forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
    }
}