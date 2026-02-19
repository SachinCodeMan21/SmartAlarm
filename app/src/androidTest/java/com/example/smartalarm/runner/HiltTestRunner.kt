package com.example.smartalarm.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom AndroidJUnitRunner to enable Hilt dependency injection in instrumentation tests.
 *
 * This runner replaces the default Application class with [dagger.hilt.android.testing.HiltTestApplication],
 * which initializes Hilt's components and dependency graph specifically for testing.
 *
 * Why use this?
 * Normally, Hilt sets up dependency injection in your app's Application class.
 * For instrumented tests, you need this custom runner to inject test dependencies
 * and allow proper component setup during tests.
 *
 * Use this runner in your `build.gradle` under `defaultConfig`:
 * ```
 * testInstrumentationRunner = "your.package.name.HiltTestRunner"
 * ```
 */
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application? {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}