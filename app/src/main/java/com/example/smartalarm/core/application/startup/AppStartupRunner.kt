package com.example.smartalarm.core.application.startup

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStartupRunner @Inject constructor(
    private val startupTasks: Set<@JvmSuppressWildcards AppStartupTask>
) {
    fun runAll() {
        startupTasks.forEach { it.start() }
    }
}
