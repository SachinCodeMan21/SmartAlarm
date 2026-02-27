package com.example.smartalarm.core.framework.di.annotations

import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Qualifier for providing the IO [CoroutineDispatcher], typically used for
 * disk or network operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier for providing the Default [CoroutineDispatcher], generally used for
 * CPU-intensive work like sorting or processing large data sets.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Qualifier for providing the Main [CoroutineDispatcher], typically used for
 * updating the UI or interacting with UI components.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
