package com.example.smartalarm.feature.stopwatch.framework.notification.model

import android.app.PendingIntent
import com.example.smartalarm.core.framework.notification.model.AppNotificationData
import com.example.smartalarm.core.framework.notification.model.NotificationAction

/**
 * Data class representing all information required to display a stopwatch notification.
 *
 * Implements [AppNotificationData] to maintain a consistent structure for notification data
 * across the app. Contains stopwatch-specific fields such as elapsed time, lap count,
 * running state, and animation progress, along with general notification fields like title,
 * content, actions, and tap behavior.
 *
 * @property id Unique identifier for the notification, typically derived from the stopwatch instance.
 * @property title The title text displayed on the notification.
 * @property contentText Description or status text for the stopwatch (e.g., "Running", "Paused").
 * @property actions A list of [NotificationAction] representing actionable buttons (e.g., pause, reset, lap) for the notification.
 * @property contentIntent An optional [PendingIntent] triggered when the user taps the notification itself.
 * @property lapCount The total number of laps recorded on the stopwatch.
 * @property isRunning True if the stopwatch is currently active, false otherwise.
 * @property progress An integer representing progress for ongoing animations (e.g., circular timer progress).
 */
data class StopwatchNotificationData(
    override val id: Int,
    override val title: String,
    override val contentText: String,
    override val actions: List<NotificationAction>,
    override val contentIntent: PendingIntent?,
    val lapCount: Int,
    val isRunning: Boolean,
    val progress: Int
) : AppNotificationData
