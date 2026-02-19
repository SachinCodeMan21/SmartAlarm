package com.example.smartalarm.feature.stopwatch.framework.di.annotations

import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationBuilderTypeKey
import dagger.MapKey


/**
 * Custom Dagger [MapKey] annotation used to bind specific
 * StopwatchNotificationBuilder instances into a map.
 *
 * This allows Dagger Hilt to provide different builders for different
 * [StopwatchNotificationBuilderTypeKey] values using multi-bindings.
 *
 * Usage:
 * ```
 * @Provides
 * @IntoMap
 * @StopwatchNotificationBuilderMapKey(StopwatchNotificationBuilderTypeKey.ACTIVE_STOPWATCH)
 * fun provideActiveStopwatchBuilder(
 *     builder: ActiveStopwatchNotificationBuilder
 * ): AppNotificationBuilder<*> = builder
 * ```
 *
 * @property value The specific [StopwatchNotificationBuilderTypeKey] that identifies the builder in the map.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class StopwatchNotificationBuilderMapKey(val value: StopwatchNotificationBuilderTypeKey)