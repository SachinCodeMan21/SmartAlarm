package com.example.smartalarm.core.notification.model

import android.app.PendingIntent

/**
 * Represents an actionable button attached to a notification.
 *
 * Each action can be tapped by the user to perform a specific operation,
 * such as snoozing an alarm, stopping a timer, or opening an activity.
 *
 * @property id Unique identifier for this action, used for tracking or handling clicks.
 * @property title The text label displayed on the action button within the notification.
 * @property icon Optional resource ID for the icon displayed on the action button.
 * @property pendingIntent The [PendingIntent] that is triggered when the action is selected.
 */
data class NotificationAction(
    val id: Int,
    val title: String,
    val icon: Int? = null,
    val pendingIntent: PendingIntent,
)
