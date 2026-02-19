package com.example.smartalarm.core.notification.factory

import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.notification.model.AppNotification
import com.example.smartalarm.core.notification.model.AppNotificationData
import com.example.smartalarm.core.notification.model.AppNotificationModel

/**
 * Factory class responsible for providing the correct [AppNotificationDataMapper]
 * instance based on a given key.
 *
 * This is useful when you have multiple types of notifications and need to
 * dynamically map their associated data to corresponding models.
 *
 * @param NM The type of the notification model extending [AppNotificationModel].
 * @param ND The type of the notification data extending [AppNotificationData].
 * @param NT The type of the notification extending [AppNotification].
 * @param K The type of key used to select the appropriate data mapper.
 * @property mappers A map of keys to their corresponding [AppNotificationDataMapper] instances.
 */
@Suppress("UNCHECKED_CAST")
abstract class NotificationDataMapperFactory<
        NM : AppNotificationModel<K, ND, NT>,
        ND : AppNotificationData,
        NT : AppNotification,
        K : AppNotificationDataMapperKey
        >(
    private val mappers: Map<K, @JvmSuppressWildcards AppNotificationDataMapper<*, *>>
) {

    /**
     * Returns the [AppNotificationDataMapper] instance associated with the given key.
     *
     * This method performs an unchecked cast based on the generic types declared in the class.
     * If no mapper is found for the given key, an [IllegalArgumentException] is thrown.
     *
     * @param key The key used to retrieve the appropriate data mapper.
     * @return The data mapper corresponding to the given key.
     * @throws IllegalArgumentException if no mapper is registered for the given key.
     */
    fun getMapper(key: K): AppNotificationDataMapper<NM, ND> {
        val mapper = mappers[key]
            ?: throw IllegalArgumentException("No mapper found for key: $key")
        return mapper as AppNotificationDataMapper<NM, ND>
    }
}
