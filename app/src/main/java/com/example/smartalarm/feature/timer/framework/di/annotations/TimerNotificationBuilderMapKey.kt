package com.example.smartalarm.feature.timer.framework.di.annotations

import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationBuilderTypeKey
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationBuilderFactory
import dagger.MapKey


/**
 * Custom Dagger Hilt map key annotation used for multibinding
 * TimerNotificationBuilder instances for timer notifications.
 *
 * Each builder is associated with a specific [TimerNotificationBuilderTypeKey],
 * allowing Dagger to create a map of builders that can be injected
 * into [TimerNotificationBuilderFactory] or other consumers.
 *
 * Usage:
 * ```
 * @Provides
 * @IntoMap
 * @TimerNotificationBuilderMapKey(TimerNotificationBuilderTypeKey.ACTIVE)
 * fun provideActiveTimerBuilder(builder: ActiveTimerNotificationBuilder): AppNotificationBuilder<*> = builder
 * ```
 *
 * @property value The key representing the type of timer notification for this builder.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class TimerNotificationBuilderMapKey(val value: TimerNotificationBuilderTypeKey)
