package com.example.smartalarm.core.framework.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class FirebaseErrorLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : ErrorLogger {

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setCustomKey(key: String, value: Any) {
        when (value) {
            is String -> crashlytics.setCustomKey(key, value)
            is Int -> crashlytics.setCustomKey(key, value)
            is Long -> crashlytics.setCustomKey(key, value)
            is Float -> crashlytics.setCustomKey(key, value)
            is Double -> crashlytics.setCustomKey(key, value)
            is Boolean -> crashlytics.setCustomKey(key, value)
        }
    }
}