package com.example.smartalarm.core.notification.model

import com.example.smartalarm.core.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder

/**
 * Represents a domain-layer model for app notifications.
 *
 * This interface defines the contract for converting domain-specific data into
 * a platform-specific notification representation using a mapping system. It
 * serves as a bridge between domain models, their corresponding data mappers,
 * and the final notification objects built by the app.
 *
 * - @param DataMapperKey The type of key used to identify the appropriate
 * [AppNotificationDataMapper]. Must implement [AppNotificationDataMapperKey].
 *
 * - @param NotificationData The raw data type containing the information needed
 * to populate the notification. Must implement [AppNotificationData].
 *
 * - @param NotificationType The final notification type that will be created
 * and passed to the notification builder. Must implement [AppNotification].
 *
 * @see AppNotificationDataMapper
 * @see AppNotificationBuilder
 */
interface AppNotificationModel<
        out DataMapperKey : AppNotificationDataMapperKey,
        NotificationData : AppNotificationData,
        NotificationType : AppNotification
        > {
    /**
     * Returns the key identifying the appropriate data mapper for this notification model.
     */
    fun getMapperKey(): DataMapperKey

    /**
     * Converts the given [NotificationData] into the final [NotificationType]
     * that can be built and displayed.
     *
     * @param data The notification data to transform.
     * @return The resulting [NotificationType] instance.
     */
    fun toNotification(data: NotificationData): NotificationType
}
