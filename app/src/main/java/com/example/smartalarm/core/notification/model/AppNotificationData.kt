package com.example.smartalarm.core.notification.model

import android.app.PendingIntent

/**
 * Base interface representing the structured data required to build an Android notification.
 *
 * Implementations of this interface provide all necessary information for constructing
 * a notification, including its identity, display content, actions, and interaction behavior.
 */
interface AppNotificationData {

    /**
     * Unique identifier for the notification, used for updating or canceling it.
     */
    val id: Int

    /**
     * Title text displayed prominently in the notification.
     */
    val title: String

    /**
     * Main content text providing additional information in the notification.
     */
    val contentText: String

    /**
     * List of actionable items (buttons) attached to the notification.
     */
    val actions: List<NotificationAction>

    /**
     * Optional [PendingIntent] that is triggered when the user taps the notification.
     */
    val contentIntent: PendingIntent?
}
