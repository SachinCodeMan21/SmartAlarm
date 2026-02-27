package com.example.smartalarm.feature.alarm.framework.notification.model

import com.example.smartalarm.core.framework.notification.model.AppNotification
import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationBuilderTypeKey

/**
 * Sealed class representing all types of alarm-related notifications in the app.
 *
 * Each subtype corresponds to a specific alarm state (e.g., upcoming, snoozed, missed, or scheduled),
 * and carries the necessary [AlarmNotificationData] to render the notification UI.
 *
 * These notifications are built at runtime using the appropriate [AppNotificationBuilder],
 * which is selected based on the notification's [key].
 */
sealed class AlarmNotification : AppNotification {

    /**
     * Notification for an **upcoming alarm**.
     *
     * Shown when an alarm is scheduled and approaching, reminding the user
     * of the next alarm time before it rings.
     *
     * @property alarmData Data required to build and display the upcoming alarm notification.
     */
    data class Upcoming(val alarmData: AlarmNotificationData) : AlarmNotification() {
        /** Key used by the factory to retrieve the correct builder for this notification type. */
        override val key = AlarmNotificationBuilderTypeKey.UPCOMING
    }

    /**
     * Notification for a **snoozed alarm**.
     *
     * Displayed after the user snoozes an alarm, indicating when it will
     * ring again after the snooze period ends.
     *
     * @property alarmData Data required to build and display the snoozed alarm notification.
     */
    data class Snoozed(val alarmData: AlarmNotificationData) : AlarmNotification() {
        /** Key used by the factory to retrieve the correct builder for this notification type. */
        override val key = AlarmNotificationBuilderTypeKey.SNOOZED
    }

    /**
     * Notification for a **missed alarm**.
     *
     * Displayed when the user fails to dismiss or snooze an alarm before it expires,
     * indicating that the alarm was missed.
     *
     * @property alarmData Data required to build and display the missed alarm notification.
     */
    data class Missed(val alarmData: AlarmNotificationData) : AlarmNotification() {
        /** Key used by the factory to retrieve the correct builder for this notification type. */
        override val key = AlarmNotificationBuilderTypeKey.MISSED
    }

    /**
     * Notification for a **scheduled or currently ringing alarm**.
     *
     * Shown while an alarm is actively scheduled or ringing, providing quick actions
     * (e.g., snooze or dismiss) and details about the current alarm.
     *
     * @property alarmData Data required to build and display the scheduled alarm notification.
     */
    data class Ringing(val alarmData: AlarmNotificationData) : AlarmNotification() {
        /** Key used by the factory to retrieve the correct builder for this notification type. */
        override val key = AlarmNotificationBuilderTypeKey.RINGING
    }

}
