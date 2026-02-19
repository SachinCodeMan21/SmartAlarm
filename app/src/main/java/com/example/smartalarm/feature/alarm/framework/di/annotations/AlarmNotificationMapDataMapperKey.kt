package com.example.smartalarm.feature.alarm.framework.di.annotations

import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationDataMapperKey
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import dagger.MapKey

/**
 * Annotation used as a map key for dependency injection bindings of
 * [AppNotificationDataMapper] instances keyed by [AlarmNotificationDataMapperKey].
 *
 * This annotation is applied on provider functions to associate them with
 * a specific [AlarmNotificationDataMapperKey] in a Dagger multiBindings map.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class AlarmNotificationMapDataMapperKey(val value: AlarmNotificationDataMapperKey)
