package com.example.smartalarm.feature.timer.framework.notification.model

import com.example.smartalarm.core.framework.notification.model.AppNotification
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationBuilderTypeKey

/**
 * Sealed class representing all types of timer-related notifications.
 *
 * Each subtype contains the data required to render a specific notification layout
 * and is resolved at runtime by the appropriate [AppNotificationBuilder]
 * based on the [key].
 */
sealed class TimerNotification : AppNotification {

    /**
     * Notification for an actively running timer.
     *
     * Displayed while the timer is running, showing remaining time
     * and any relevant controls or information.
     *
     * @property data The data needed to build and display this notification.
     */
    data class ActiveTimer(val data: TimerNotificationData) : TimerNotification() {
        /** Key used by the factory to select the correct builder for this notification type. */
        override val key = TimerNotificationBuilderTypeKey.ACTIVE
    }

    /**
     * Notification for a completed timer.
     *
     * Displayed when the timer finishes, indicating completion
     * and allowing actions like dismissing or restarting.
     *
     * @property data The data needed to build and display this notification.
     */
    data class CompletedTimer(val data: TimerNotificationData) : TimerNotification() {
        /** Key used by the factory to select the correct builder for this notification type. */
        override val key = TimerNotificationBuilderTypeKey.COMPLETED
    }

    data class MissedTimer(val data: TimerNotificationData) : TimerNotification() {
        /** Key used by the factory to select the correct builder for this notification type. */
        override val key = TimerNotificationBuilderTypeKey.MISSED
    }
}
