package com.example.smartalarm.feature.alarm.framework.notification.factory

import android.content.Context
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.framework.notification.factory.AppNotificationBuilderFactory
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationBuilderTypeKey
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Factory responsible for building [AppNotificationBuilder] instances
 * specific to [AlarmNotification] types.
 *
 * This factory leverages Dagger multi-bindings to inject a map of notification builders,
 * each keyed by [AlarmNotificationBuilderTypeKey]. The actual resolution and invocation
 * of the correct builder is delegated to the base class [AppNotificationBuilderFactory],
 * ensuring type-safe, centralized construction of alarm-related notification builders.
 *
 * By using this factory, different alarm notification types
 * (e.g., upcoming, snoozed, missed, scheduled) can be handled dynamically
 * without requiring separate factory logic for each type.
 *
 * @constructor Injects the application [context] and a map of builder instances.
 *
 * @param context The application context used by the base factory for building notifications.
 * @param builders A map of [AppNotificationBuilder] instances keyed by [AlarmNotificationBuilderTypeKey].
 *
 * @see AppNotificationBuilderFactory
 * @see AlarmNotification
 * @see AlarmNotificationBuilderTypeKey
 */
class AlarmNotificationBuilderFactory @Inject constructor(
    @ApplicationContext context: Context,
    builders: Map<AlarmNotificationBuilderTypeKey, @JvmSuppressWildcards AppNotificationBuilder<*>>
) : AppNotificationBuilderFactory<
        AlarmNotification,
        AlarmNotificationBuilderTypeKey,
        AppNotificationBuilder<AlarmNotification>
        >(context, builders)
