package com.example.smartalarm.feature.alarm.framework.di.annotations

import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationBuilderTypeKey
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import dagger.MapKey

/**
 * Custom Dagger [MapKey] annotation used to bind specific
 * [AppNotificationBuilder] instances for alarms into a map.
 *
 * This annotation allows Dagger Hilt to associate each provider function
 * with a specific [AlarmNotificationBuilderTypeKey] when using multibindings.
 * It enables type-safe retrieval of the correct alarm notification builder
 * based on its key.
 *
 * Usage example:
 * ```
 * @Provides
 * @IntoMap
 * @AlarmNotificationBuilderMapKey(AlarmNotificationBuilderTypeKey.UPCOMING)
 * fun provideUpcomingAlarmBuilder(
 *     builder: UpcomingAlarmNotificationBuilder
 * ): AppNotificationBuilder<*> = builder
 * ```
 *
 * @property value The [AlarmNotificationBuilderTypeKey] used as the key in the map.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class AlarmNotificationBuilderMapKey(val value: AlarmNotificationBuilderTypeKey)