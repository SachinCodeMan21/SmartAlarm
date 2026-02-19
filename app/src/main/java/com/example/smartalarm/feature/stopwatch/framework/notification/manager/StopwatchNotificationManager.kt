package com.example.smartalarm.feature.stopwatch.framework.notification.manager

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.notification.manager.AppNotificationManager
import com.example.smartalarm.feature.stopwatch.framework.notification.factory.StopwatchNotificationBuilderFactory
import com.example.smartalarm.feature.stopwatch.framework.notification.factory.StopwatchNotificationDataMapperFactory
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class StopwatchNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    val builderFactory: StopwatchNotificationBuilderFactory,
    val notificationManager: NotificationManagerCompat,
    val mapperFactory: StopwatchNotificationDataMapperFactory
) : AppNotificationManager(context, notificationManager) {

    fun getStopwatchNotification(model: StopwatchNotificationModel): Notification {
        val mapper = mapperFactory.getMapper(model.getMapperKey())
        val data = mapper.map(context, model)
        val notificationType = model.toNotification(data)
        return builderFactory.buildNotification(notificationType)
    }

    fun updateStopwatchNotification(notificationId: Int, model: StopwatchNotificationModel) {
        val stopwatchNotification = getStopwatchNotification(model)
        postNotification(notificationId, stopwatchNotification)
    }

}
