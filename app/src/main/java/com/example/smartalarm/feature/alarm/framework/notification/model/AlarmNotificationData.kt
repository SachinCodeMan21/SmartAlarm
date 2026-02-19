package com.example.smartalarm.feature.alarm.framework.notification.model

import android.app.PendingIntent
import com.example.smartalarm.core.notification.model.AppNotificationData
import com.example.smartalarm.core.notification.model.NotificationAction
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity

/**
 * Data class representing the content and behavior of an alarm notification.
 *
 * This class encapsulates all the information needed to display an alarm notification,
 * including the notification's identifier, title, main text, actionable buttons,
 * and the intent to be triggered when the notification is tapped.
 *
 * Implements [AppNotificationData] to provide a consistent structure for notification data across the app.
 *
 * @property id Unique identifier for the notification, typically the alarm ID.
 * @property title The title text displayed on the notification.
 * @property contentText The main content or message text shown on the notification.
 * @property actions A list of [NotificationAction]s representing the buttons or actions available on the notification.
 * @property contentIntent An optional [PendingIntent] fired when the user taps the notification itself.
 * @property fullScreenIntent An optional [PendingIntent] that launches [AlarmActivity] in full-screen mode.
 *
 */
data class AlarmNotificationData(
    override val id: Int,
    override val title: String,
    override val contentText: String,
    override val actions: List<NotificationAction>,
    override val contentIntent: PendingIntent?,
    val fullScreenIntent: PendingIntent? = null,
) : AppNotificationData
