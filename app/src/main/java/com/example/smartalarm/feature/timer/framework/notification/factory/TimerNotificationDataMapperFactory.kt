package com.example.smartalarm.feature.timer.framework.notification.factory

import com.example.smartalarm.core.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationDataMapperKey
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotification
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationData
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import javax.inject.Inject

class TimerNotificationDataMapperFactory @Inject constructor(
    mappers: Map<TimerNotificationDataMapperKey, @JvmSuppressWildcards AppNotificationDataMapper<*, *>>
) : NotificationDataMapperFactory<
        TimerNotificationModel,
        TimerNotificationData,
        TimerNotification,
        TimerNotificationDataMapperKey
        >(mappers)