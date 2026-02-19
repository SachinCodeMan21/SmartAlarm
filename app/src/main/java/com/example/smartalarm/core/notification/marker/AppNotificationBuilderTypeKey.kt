package com.example.smartalarm.core.notification.marker

import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.factory.AppNotificationBuilderFactory
import android.app.Notification

/**
 * Marker interface representing a unique key for an [AppNotificationBuilder].
 *
 * Implementations of this interface (usually enums or sealed objects) serve as identifiers
 * for specific notification types, allowing the system to determine which builder
 * should be used to create a platform-specific [Notification].
 *
 * These keys are utilized by [AppNotificationBuilderFactory] to map notification types
 * to their corresponding builder implementations, enabling:
 * - Flexible support for multiple notification types.
 * - Centralized construction logic for notifications.
 * - Easy extension for new notification layouts, priorities, or behaviors.
 */
interface AppNotificationBuilderTypeKey
