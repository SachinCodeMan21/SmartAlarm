package com.example.smartalarm.core.framework.notification.builder

import android.app.Notification
import android.content.Context
import com.example.smartalarm.core.framework.notification.model.AppNotification
import com.example.smartalarm.core.framework.notification.factory.AppNotificationBuilderFactory

/**
 * Defines a contract for building Android [Notification] instances
 * for a specific subtype of [AppNotification].
 *
 * Each implementation is responsible for constructing a fully configured [Notification]
 * (title, text, actions, icons, etc.) tailored to its notification type.
 *
 * Currently used by:
 * - **Alarm notifications** — e.g., upcoming, snoozed, missed, or ringing alarms.
 * - **Timer notifications** — e.g., running or completed timers.
 * - **Stopwatch notifications** — e.g., active or paused stopwatches.
 *
 * This interface enables a flexible and extensible notification architecture:
 * new features (such as reminders or calendar events) can introduce their own
 * notification types by implementing this interface and registering them with
 * the [AppNotificationBuilderFactory].
 *
 * @param T The specific subtype of [AppNotification] this builder supports.
 */
interface AppNotificationBuilder<T : AppNotification> {

    /**
     * Builds a notification for the given [notificationType].
     *
     * @param context The context to use for building the notification.
     * @param notificationType The specific notification data to build.
     * @return The constructed [Notification].
     */
    fun buildNotification(context: Context, notificationType: T): Notification

}