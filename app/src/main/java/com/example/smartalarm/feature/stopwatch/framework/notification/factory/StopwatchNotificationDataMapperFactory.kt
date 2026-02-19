package com.example.smartalarm.feature.stopwatch.framework.notification.factory

import com.example.smartalarm.core.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationDataMapperKey
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotification
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationData
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationModel
import javax.inject.Inject

/**
 * Factory class responsible for creating the appropriate [AppNotificationDataMapper]
 * for stopwatch notifications based on the given key.
 *
 * This factory utilizes a map of mappers where each entry is associated with a
 * [StopwatchNotificationDataMapperKey] and an [AppNotificationDataMapper]. It ensures
 * that the correct data mapper is used to transform stopwatch notification data into
 * the desired notification model.
 *
 * @param mappers A map of [StopwatchNotificationDataMapperKey] to [AppNotificationDataMapper]
 *        implementations that handle the transformation between various stopwatch
 *        notification data types and models.
 */
class StopwatchNotificationDataMapperFactory @Inject constructor(
    mappers: Map<StopwatchNotificationDataMapperKey, @JvmSuppressWildcards AppNotificationDataMapper<*, *>>
) : NotificationDataMapperFactory<
        StopwatchNotificationModel,
        StopwatchNotificationData,
        StopwatchNotification,
        StopwatchNotificationDataMapperKey
        >(mappers)
