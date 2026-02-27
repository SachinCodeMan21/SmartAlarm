package com.example.smartalarm.feature.alarm.framework.notification.model

import com.example.smartalarm.core.framework.notification.model.AppNotificationModel
import com.example.smartalarm.core.framework.notification.model.GroupableNotification
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationDataMapperKey

/**
 * Sealed class representing the domain-layer models for alarm notifications.
 *
 * Each model maps domain-specific [AlarmModel] data to a corresponding
 * [AlarmNotificationData], which is then converted into a typed
 * [AlarmNotification] for display in the UI.
 *
 * Implements [AppNotificationModel] with the following type parameters:
 * - [AlarmNotificationDataMapperKey]: identifies the appropriate data mapper,
 * - [AlarmNotificationData]: the structured notification data produced by the mapper,
 * - [AlarmNotification]: the final notification UI model built by the factory.
 *
 * Each subclass represents a specific alarm state and provides:
 * - The underlying alarm data ([AlarmModel]),
 * - The mapper key used to select the correct data mapper,
 * - A method to convert the mapped data into a typed [AlarmNotification].
 */
sealed class AlarmNotificationModel : AppNotificationModel<
        AlarmNotificationDataMapperKey,
        AlarmNotificationData,
        AlarmNotification
        > {

    /**
     * Model representing an **upcoming alarm** notification.
     *
     * @property alarm The domain-layer alarm data.
     * @property nextAlarmTimeInMillis The timestamp of the next alarm.
     */
    data class UpcomingAlarmModel(val alarm: AlarmModel, val nextAlarmTimeInMillis: Long) : AlarmNotificationModel(), GroupableNotification {

        /** Returns the mapper key for upcoming alarms. */
        override fun getMapperKey(): AlarmNotificationDataMapperKey = AlarmNotificationDataMapperKey.UPCOMING

        /** Converts the mapped data into an [AlarmNotification.Upcoming] instance. */
        override fun toNotification(data: AlarmNotificationData): AlarmNotification = AlarmNotification.Upcoming(data)

        override val groupKey: String = NotificationGroupKeys.UPCOMING_ALARMS

    }

    /**
     * Model representing a **missed alarm** notification.
     *
     * @property alarm The domain-layer alarm data.
     */
    data class MissedAlarmModel(val alarm: AlarmModel) : AlarmNotificationModel(), GroupableNotification {

        /** Returns the mapper key for missed alarms. */
        override fun getMapperKey(): AlarmNotificationDataMapperKey =
            AlarmNotificationDataMapperKey.MISSED

        /** Converts the mapped data into an [AlarmNotification.Missed] instance. */
        override fun toNotification(data: AlarmNotificationData): AlarmNotification =
            AlarmNotification.Missed(data)

        override val groupKey: String = NotificationGroupKeys.MISSED_ALARMS
    }

    /**
     * Model representing a **snoozed alarm** notification.
     *
     * @property alarm The domain-layer alarm data.
     */
    data class SnoozedAlarmModel(
        val alarm: AlarmModel,
        val snoozeTimeInMillis: Long
    ) : AlarmNotificationModel(), GroupableNotification {

        /** Returns the mapper key for snoozed alarms. */
        override fun getMapperKey(): AlarmNotificationDataMapperKey =
            AlarmNotificationDataMapperKey.SNOOZED

        /** Converts the mapped data into an [AlarmNotification.Snoozed] instance. */
        override fun toNotification(data: AlarmNotificationData): AlarmNotification = AlarmNotification.Snoozed(data)

        override val groupKey: String = NotificationGroupKeys.SNOOZED_ALARMS
    }

    /**
     * Model representing a **scheduled or currently active/ringing alarm** notification.
     *
     * @property alarm The domain-layer alarm data.
     */
    data class RingingAlarmModel(val alarm: AlarmModel) : AlarmNotificationModel() {

        /** Returns the mapper key for scheduled alarms. */
        override fun getMapperKey(): AlarmNotificationDataMapperKey =
            AlarmNotificationDataMapperKey.SCHEDULED

        /** Converts the mapped data into an [AlarmNotification.Ringing] instance. */
        override fun toNotification(data: AlarmNotificationData): AlarmNotification =
            AlarmNotification.Ringing(data)
    }
}
