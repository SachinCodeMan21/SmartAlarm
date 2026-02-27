package com.example.smartalarm.feature.alarm.framework.notification.factory

import com.example.smartalarm.core.framework.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationDataMapperKey
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotification
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import javax.inject.Inject


/**
 * Factory class responsible for providing the appropriate [AppNotificationDataMapper] implementation
 * based on the type of [AlarmNotificationModel].
 *
 * This factory uses a map of [AlarmNotificationDataMapperKey] to [AppNotificationDataMapper] instances,
 * allowing the system to dynamically select the correct mapper for converting an alarm model
 * into its corresponding [AlarmNotificationData].
 *
 * This is used during the alarm notification pipeline where different alarm states
 * (e.g., UPCOMING, SNOOZED, MISSED, SCHEDULED) require different mappers to build
 * the notification data structure.
 *
 * @constructor Injects the map of data mappers into the factory.
 * @param mappers A map of [AlarmNotificationDataMapperKey] to [AppNotificationDataMapper]s used
 *                to map alarm models to notification data.
 */
class AlarmNotificationDataMapperFactory @Inject constructor(
    mappers: Map<AlarmNotificationDataMapperKey, @JvmSuppressWildcards AppNotificationDataMapper<*, *>>
) : NotificationDataMapperFactory<
        AlarmNotificationModel,
        AlarmNotificationData,
        AlarmNotification,
        AlarmNotificationDataMapperKey
        >(mappers)
