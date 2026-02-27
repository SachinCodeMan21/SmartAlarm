package com.example.smartalarm.core.framework.notification.factory

import android.app.Notification
import android.content.Context
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.framework.notification.marker.AppNotificationBuilderTypeKey
import com.example.smartalarm.core.framework.notification.model.AppNotification

/**
 * A generic factory responsible for creating [Notification] instances
 * by delegating to the correct [AppNotificationBuilder] implementation
 * based on a notification's unique type key.
 *
 * This factory abstracts the logic of selecting the appropriate builder
 * for a given [AppNotification] subtype. Each concrete factory (e.g., for alarms,
 * timers, or stopwatches) provides a mapping between its own
 * [AppNotificationBuilderTypeKey] values and the corresponding builder instances.
 *
 * ### Type Parameters
 * - **notificationType** — The specific subtype of [AppNotification] handled by this factory.
 * - **builderTypeKey** — The key type used to identify notification builder implementations.
 * - **builderType** — The builder type responsible for constructing notifications of type [notificationType].
 *
 * @property context The [Context] used to build notifications (for resources, channels, etc.).
 * @property builders A map linking [builderTypeKey] instances to their corresponding [AppNotificationBuilder]s.
 *
 * @throws IllegalArgumentException if no registered builder matches the given notification's key.
 *
 * @see AppNotification
 * @see AppNotificationBuilder
 * @see AppNotificationBuilderTypeKey
 */
@Suppress("UNCHECKED_CAST")
abstract class AppNotificationBuilderFactory<
        notificationType : AppNotification,
        builderTypeKey : AppNotificationBuilderTypeKey,
        builderType : AppNotificationBuilder<notificationType>
        >(
    private val context: Context,
    private val builders: Map<builderTypeKey, @JvmSuppressWildcards AppNotificationBuilder<*>>
) {
    /**
     * Builds a [Notification] for the provided [notificationType]
     * by resolving and invoking the appropriate builder from the [builders] map.
     *
     * The builder is selected using the [AppNotificationBuilderTypeKey]
     * obtained from [notificationType.key]. If no builder is registered for that key,
     * an [IllegalArgumentException] is thrown.
     *
     * @param notificationType The [AppNotification] instance describing the notification to build.
     * @return A fully constructed [Notification] built by the corresponding [AppNotificationBuilder].
     *
     * @throws IllegalArgumentException If no matching builder is found for the given notification key.
     */
    fun buildNotification(notificationType: notificationType): Notification {
        val builder = builders[notificationType.key as builderTypeKey]
            ?: throw IllegalArgumentException("No builder found for key: ${notificationType.key}")
        return (builder as builderType).buildNotification(context, notificationType)
    }
}
