package com.example.smartalarm.feature.stopwatch.framework.notification.factory

import android.content.Context
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.framework.notification.factory.AppNotificationBuilderFactory
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationBuilderTypeKey
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Factory responsible for creating [AppNotificationBuilder] instances
 * specific to [StopwatchNotification] types.
 *
 * This factory uses Dagger multi-bindings to inject a map of notification builders,
 * keyed by [StopwatchNotificationBuilderTypeKey]. The resolution of the correct builder
 * is delegated to the base class [AppNotificationBuilderFactory], providing
 * a centralized and type-safe way to construct stopwatch-related notification builders.
 *
 * Different stopwatch notification types (e.g., active, paused) can be
 * handled dynamically without requiring separate factory logic for each type.
 *
 * @constructor Injects the application [context] and a map of builder instances.
 *
 * @param context The application context used by the base factory to build notifications.
 * @param builders A map of [AppNotificationBuilder] instances keyed by [StopwatchNotificationBuilderTypeKey].
 *
 * @see AppNotificationBuilderFactory
 * @see StopwatchNotification
 * @see StopwatchNotificationBuilderTypeKey
 */
class StopwatchNotificationBuilderFactory @Inject constructor(
    @ApplicationContext context: Context,
    builders: Map<StopwatchNotificationBuilderTypeKey, @JvmSuppressWildcards AppNotificationBuilder<*>>
) : AppNotificationBuilderFactory<
        StopwatchNotification,
        StopwatchNotificationBuilderTypeKey,
        AppNotificationBuilder<StopwatchNotification>
        >(context, builders)

