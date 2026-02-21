package com.example.smartalarm.feature.alarm.framework.notification.enums

import com.example.smartalarm.core.notification.marker.AppNotificationBuilderTypeKey
import com.example.smartalarm.feature.alarm.framework.notification.factory.AlarmNotificationBuilderFactory
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder

/**
 * Enum representing the different types of alarm notifications that require distinct builders.
 *
 * Used as a key in the [AlarmNotificationBuilderFactory] to map each alarm notification type
 * (e.g., upcoming, missed, snoozed, scheduled) to its corresponding [AppNotificationBuilder].
 *
 * This allows the app to delegate the construction of notifications to the appropriate builder
 * based on the alarm's current state.
 */
enum class AlarmNotificationBuilderTypeKey : AppNotificationBuilderTypeKey {

    /** Represents an upcoming alarm notification (before the alarm rings). */
    UPCOMING,

    /** Represents a missed alarm notification (user did not respond to alarm). */
    MISSED,

    /** Represents a snoozed alarm notification (user postponed the alarm). */
    SNOOZED,

    /** Represents a currently ringing or scheduled alarm notification. */
    RINGING
}
