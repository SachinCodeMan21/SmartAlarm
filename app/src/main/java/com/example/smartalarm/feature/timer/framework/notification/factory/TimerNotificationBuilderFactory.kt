package com.example.smartalarm.feature.timer.framework.notification.factory

import android.content.Context
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.framework.notification.factory.AppNotificationBuilderFactory
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationBuilderTypeKey
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Factory responsible for creating [AppNotificationBuilder] instances
 * specific to [TimerNotification] types.
 *
 * This factory leverages Dagger multi-bindings to inject a map of notification builders,
 * keyed by [TimerNotificationBuilderTypeKey]. The actual selection and invocation
 * of the appropriate builder is delegated to the base class [AppNotificationBuilderFactory],
 * ensuring type-safe and centralized construction of timer-related notification builders.
 *
 * Different timer notification types (e.g., running, completed) can be
 * handled dynamically without requiring separate factory logic for each type.
 *
 * @constructor Injects the application [context] and a map of builder instances.
 *
 * @param context The application context used by the base factory for building notifications.
 * @param builders A map of [AppNotificationBuilder] instances keyed by [TimerNotificationBuilderTypeKey].
 *
 * @see AppNotificationBuilderFactory
 * @see TimerNotification
 * @see TimerNotificationBuilderTypeKey
 */
class TimerNotificationBuilderFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    builders: Map<TimerNotificationBuilderTypeKey, @JvmSuppressWildcards AppNotificationBuilder<*>>
) : AppNotificationBuilderFactory<
        TimerNotification,
        TimerNotificationBuilderTypeKey,
        AppNotificationBuilder<TimerNotification>>(context, builders)
