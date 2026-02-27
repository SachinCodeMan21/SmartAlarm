package com.example.smartalarm.core.framework.notification.model

/**
 * Holds the data extracted from a notification to be used for navigating the app.
 * This data includes the destination to navigate to, the action related to the notification,
 * and any additional ID that may be required for the navigation or action.
 *
 * Key Fields:
 * - **destinationId**: ID of the fragment or screen to navigate to.
 * - **notificationAction**: Type of action related to the notification (e.g., timer action).
 * - **extraId**: Optional extra data ID, used for additional information required for the action (can be null).
 */
data class NotificationIntentData(
    val destinationId: Int,
    val notificationAction: String,
    val extraId: Int?
)
