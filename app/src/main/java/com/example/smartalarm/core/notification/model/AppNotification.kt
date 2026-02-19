package com.example.smartalarm.core.notification.model

import com.example.smartalarm.core.notification.marker.AppNotificationBuilderTypeKey
import com.example.smartalarm.core.notification.factory.AppNotificationBuilderFactory
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder

/**
 * Base interface representing a notification within the app.
 *
 * Implementations of this interface define specific types of notifications
 * (e.g., alarm, timer, stopwatch) and carry the necessary data to build them.
 *
 * Each notification exposes a [key] that identifies its type and is used
 * by [AppNotificationBuilderFactory] to select the appropriate
 * [AppNotificationBuilder] for constructing the notification.
 */
interface AppNotification {

    /**
     * The key representing the type of this notification.
     *
     * This key is used for mapping the notification to its corresponding builder
     * in a type-safe manner.
     */
    val key: AppNotificationBuilderTypeKey

}
