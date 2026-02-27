package com.example.smartalarm.feature.stopwatch.framework.notification.model

import com.example.smartalarm.core.framework.notification.model.AppNotificationModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationDataMapperKey

/**
 * Sealed class representing domain-layer models for stopwatch notifications.
 *
 * Each model maps a domain-specific [StopwatchModel] to a corresponding
 * [StopwatchNotificationData], which is then converted into a typed
 * [StopwatchNotification] for display in the UI.
 *
 * Implements [AppNotificationModel] with the following type parameters:
 * - [StopwatchNotificationDataMapperKey]: identifies the appropriate data mapper,
 * - [StopwatchNotificationData]: the structured notification data produced by the mapper,
 * - [StopwatchNotification]: the final notification UI model built by the factory.
 *
 * Each subclass represents a specific stopwatch state and provides:
 * - The underlying stopwatch data ([StopwatchModel]),
 * - The mapper key used to select the correct data mapper,
 * - A method to convert mapped data into a typed [StopwatchNotification].
 */
sealed class StopwatchNotificationModel : AppNotificationModel<StopwatchNotificationDataMapperKey, StopwatchNotificationData, StopwatchNotification> {

    /**
     * Model representing an **active stopwatch** notification.
     *
     * @property stopwatch The underlying stopwatch domain data.
     */
    data class ActiveStopwatchModel(
        val stopwatch: StopwatchModel,
    ) : StopwatchNotificationModel() {

        /** Returns the mapper key for active stopwatch notifications. */
        override fun getMapperKey(): StopwatchNotificationDataMapperKey =
            StopwatchNotificationDataMapperKey.ACTIVE

        /** Converts the mapped data into a [StopwatchNotification.ActiveStopwatch] instance. */
        override fun toNotification(data: StopwatchNotificationData): StopwatchNotification =
            StopwatchNotification.ActiveStopwatch(data)
    }
}
