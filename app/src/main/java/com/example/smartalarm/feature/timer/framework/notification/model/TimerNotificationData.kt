package com.example.smartalarm.feature.timer.framework.notification.model

import android.app.PendingIntent
import com.example.smartalarm.core.framework.notification.model.AppNotificationData
import com.example.smartalarm.core.framework.notification.model.NotificationAction
import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Data class representing all necessary information to display a timer notification.
 *
 * Implements [AppNotificationData] to provide a consistent structure for notification
 * data across the app. Contains timer-specific information as well as general
 * notification details like title, content, actions, and tap behavior.
 *
 * @property timer The [TimerModel] representing the current timer state and details.
 * @property formattedBigText A formatted string showing detailed timer information, typically used for expanded notification layouts.
 * @property id Unique identifier for the notification, usually derived from the timer ID.
 * @property title The title text displayed on the notification.
 * @property contentText The main content or message text shown on the notification.
 * @property actions A list of [NotificationAction] representing actionable buttons available on the notification.
 * @property contentIntent An optional [PendingIntent] triggered when the user taps the notification itself.
 */
data class TimerNotificationData(
    val timer: TimerModel,
    val formattedBigText: String,
    override val id: Int,
    override val title: String,
    override val contentText: String,
    override val actions: List<NotificationAction>,
    override val contentIntent: PendingIntent?
) : AppNotificationData
