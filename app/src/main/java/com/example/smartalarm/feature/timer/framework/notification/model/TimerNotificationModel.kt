package com.example.smartalarm.feature.timer.framework.notification.model

import com.example.smartalarm.core.notification.model.AppNotificationModel
import com.example.smartalarm.core.notification.model.GroupableNotification
import com.example.smartalarm.feature.alarm.framework.notification.model.NotificationGroupKeys
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationDataMapperKey

/**
 * Sealed class representing domain-layer models for timer notifications.
 *
 * Each model maps a domain-specific [TimerModel] to a corresponding
 * [TimerNotificationData], which is then converted into a typed
 * [TimerNotification] for display in the UI.
 *
 * Implements [AppNotificationModel] with the following type parameters:
 * - [TimerNotificationDataMapperKey]: identifies the appropriate data mapper,
 * - [TimerNotificationData]: the structured notification data produced by the mapper,
 * - [TimerNotification]: the final notification UI model built by the factory.
 *
 * Each subclass represents a specific timer state and provides:
 * - The underlying timer data ([TimerModel]),
 * - The mapper key used to select the correct data mapper,
 * - A method to convert mapped data into a typed [TimerNotification].
 */
sealed class TimerNotificationModel : AppNotificationModel<
        TimerNotificationDataMapperKey,
        TimerNotificationData,
        TimerNotification
        > {

    /**
     * Model representing an ACTIVE timer notification.
     *
     * @property timer Representative timer (used for title, actions, etc.)
     * @property totalCount Total number of timers in this group
     * @property runningCount Number of currently running timers
     * @property pausedCount Number of currently paused timers
     */
    data class ActiveTimerModel(
        val timer: TimerModel,
        val totalCount: Int,
        val runningCount: Int,
        val pausedCount: Int
    ) : TimerNotificationModel() {

        override fun getMapperKey(): TimerNotificationDataMapperKey =
            TimerNotificationDataMapperKey.ACTIVE

        override fun toNotification(data: TimerNotificationData): TimerNotification =
            TimerNotification.ActiveTimer(data)
    }

    /**
     * Model representing a COMPLETED timer notification.
     *
     * @property timer Representative timer
     * @property totalCount Total timers shown
     * @property runningCount Running active timers (if any)
     * @property pausedCount Paused active timers (if any)
     * @property completedCount Number of completed timers
     */
    data class CompletedTimerModel(
        val timer: TimerModel,
        val totalCount: Int,
        val runningCount: Int,
        val pausedCount: Int,
        val completedCount: Int
    ) : TimerNotificationModel() {

        override fun getMapperKey(): TimerNotificationDataMapperKey =
            TimerNotificationDataMapperKey.COMPLETED

        override fun toNotification(data: TimerNotificationData): TimerNotification =
            TimerNotification.CompletedTimer(data)
    }

    data class MissedTimerModel(
        val timer: TimerModel,
    ) : TimerNotificationModel(), GroupableNotification {

        override fun getMapperKey(): TimerNotificationDataMapperKey =
            TimerNotificationDataMapperKey.MISSED

        override fun toNotification(data: TimerNotificationData): TimerNotification =
            TimerNotification.MissedTimer(data)

        override val groupKey: String
            get() = NotificationGroupKeys.MISSED_TIMERS
    }
}