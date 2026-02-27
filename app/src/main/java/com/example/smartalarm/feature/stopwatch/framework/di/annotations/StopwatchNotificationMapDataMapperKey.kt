package com.example.smartalarm.feature.stopwatch.framework.di.annotations

import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationDataMapperKey
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import dagger.MapKey

/**
 * Custom Dagger [MapKey] annotation used to bind specific
 * [AppNotificationDataMapper] instances into a map.
 *
 * This allows Dagger Hilt to provide different data mappers for different
 * [StopwatchNotificationDataMapperKey] values using multi-bindings.
 *
 * ### Usage example:
 * ```
 * @Provides
 * @IntoMap
 * @StopwatchNotificationMapDataMapperKey(StopwatchNotificationDataMapperKey.ACTIVE)
 * fun provideStopwatchActiveDataMapper(
 *     mapper: ActiveStopwatchNotificationDataMapper
 * ): AppNotificationDataMapper<*, *> = mapper
 * ```
 *
 * @property value The specific [StopwatchNotificationDataMapperKey] that identifies
 * the data mapper in the Dagger-provided map.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class StopwatchNotificationMapDataMapperKey(val value: StopwatchNotificationDataMapperKey)
