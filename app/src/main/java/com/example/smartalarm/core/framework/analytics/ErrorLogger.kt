package com.example.smartalarm.core.framework.analytics

interface ErrorLogger {
    fun recordException(throwable: Throwable)
    fun log(message: String)

    fun setCustomKey(key: String, value: Any)
}