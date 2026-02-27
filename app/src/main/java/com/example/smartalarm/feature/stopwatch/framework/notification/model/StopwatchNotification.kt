package com.example.smartalarm.feature.stopwatch.framework.notification.model

import com.example.smartalarm.core.framework.notification.model.AppNotification
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationBuilderTypeKey
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder


/**
 * Sealed class representing all types of stopwatch-related notifications.
 *
 * Each subtype contains the data required to render a specific notification layout
 * and is resolved at runtime by the appropriate [AppNotificationBuilder]
 * based on the [key].
 */
sealed class StopwatchNotification : AppNotification {

    /**
     * Notification for an actively running stopwatch.
     *
     * Displayed while the stopwatch is running, showing elapsed time
     * and any relevant controls or information.
     *
     * @property data The data needed to build and display this notification.
     */
    data class ActiveStopwatch(val data: StopwatchNotificationData) : StopwatchNotification() {

        /** Key used by the factory to select the correct builder for this notification type. */
        override val key = StopwatchNotificationBuilderTypeKey.ACTIVE_STOPWATCH
    }
}
