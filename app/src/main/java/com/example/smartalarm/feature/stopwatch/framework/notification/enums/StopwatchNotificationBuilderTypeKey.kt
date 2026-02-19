package com.example.smartalarm.feature.stopwatch.framework.notification.enums

import com.example.smartalarm.core.notification.marker.AppNotificationBuilderTypeKey

/**
 * Enum representing the different types of stopwatch notifications.
 *
 * Used as a key to select the appropriate notification builder in the factory.
 */
enum class StopwatchNotificationBuilderTypeKey : AppNotificationBuilderTypeKey {
    /** Notification type for an active stopwatch. */
    ACTIVE_STOPWATCH
}