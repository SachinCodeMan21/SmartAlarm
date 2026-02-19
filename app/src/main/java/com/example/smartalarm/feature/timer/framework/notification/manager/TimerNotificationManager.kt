package com.example.smartalarm.feature.timer.framework.notification.manager

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.notification.manager.AppNotificationManager
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationBuilderFactory
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationDataMapperFactory
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimerNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    val builderFactory: TimerNotificationBuilderFactory,
    notificationManager: NotificationManagerCompat,
    val mapperFactory: TimerNotificationDataMapperFactory
) : AppNotificationManager(context, notificationManager) {


     fun getTimerNotification(model: TimerNotificationModel): Notification {
        val mapper = mapperFactory.getMapper(model.getMapperKey())
        val data = mapper.map(context, model)
        val notificationType = model.toNotification(data)
        return builderFactory.buildNotification(notificationType)
    }

    fun postTimerNotification(notificationId : Int, timerNotificationModel: TimerNotificationModel){
        val notification = getTimerNotification(timerNotificationModel)
        postNotification(notificationId,notification)
    }

    fun cancelTimerNotification(notificationId : Int){
        cancelNotification(notificationId)
    }

    fun updateTimerNotification(notificationId : Int, notification: Notification) {
        postNotification(notificationId,notification)
    }


}