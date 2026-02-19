package com.example.smartalarm.feature.alarm.framework.notification.manager

import android.app.Notification
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.notification.manager.AppNotificationManager
import com.example.smartalarm.core.notification.model.GroupableNotification
import com.example.smartalarm.feature.alarm.framework.notification.factory.AlarmNotificationBuilderFactory
import com.example.smartalarm.feature.alarm.framework.notification.factory.AlarmNotificationDataMapperFactory
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationGroup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Manages Alarm-specific notifications, including complex grouping logic
 * for upcoming, missed, and snoozed alarms.
 */
class AlarmNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val builderFactory: AlarmNotificationBuilderFactory,
    private val notificationManager: NotificationManagerCompat,
    private val mapperFactory: AlarmNotificationDataMapperFactory,
) : AppNotificationManager(context, notificationManager) {

    /**
     * Maps domain models to Android Notifications using the specialized factory and mapper.
     */
    fun getAlarmNotification(model: AlarmNotificationModel): Notification {
        val mapper = mapperFactory.getMapper(model.getMapperKey())
        val data = mapper.map(context, model)
        val notificationType = model.toNotification(data)
        return builderFactory.buildNotification(notificationType)
    }

    /**
     * Posts an alarm notification and updates the associated group summary if needed.
     */
    fun postAlarmNotification(notificationId: Int, model: AlarmNotificationModel) {

        // 1. Post the primary notification
        val notification = getAlarmNotification(model)
        postNotification(notificationId, notification)

        // 2. Handle grouping logic if the model is groupable
        if (model is GroupableNotification) {
            updateAlarmGroupSummaryNotification(model.groupKey)
        }
    }

    /**
     * Cancels a notification and intelligently cleans up or updates the group header.
     */
    fun cancelAlarmNotification(notificationId: Int) {

        val removed = getActiveNotification(notificationId)
        val groupKey = removed?.notification?.group

        // 1. Cancel the child notification
        cancelNotification(notificationId)

        // 2. Update group summary if this notification belonged to a group
        if (groupKey != null) {
            updateAlarmGroupSummaryNotification(groupKey)
        }

    }



    // --- Private Helper Methods ---

    private fun getAlarmGroupSummaryNotification(groupKey: String): Notification? {

        val  notificationGroup = AlarmNotificationGroup.fromAlarmGroupKey(groupKey)

        if (notificationGroup == null) return null

        return NotificationCompat.Builder(context, notificationGroup.channelId)
            .setContentTitle(notificationGroup.title)
            .setContentText(notificationGroup.defaultContent)
            .setSmallIcon(notificationGroup.smallResIconId)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Ensures the group summary notification is in the correct state
     * based on currently active child notifications.
     */
    private fun updateAlarmGroupSummaryNotification(groupKey: String) {

        val active = notificationManager.activeNotifications

        val childNotifications = active.filter {
            it.notification.group == groupKey && it.id != groupKey.hashCode()
        }

        val summaryId = groupKey.hashCode()

        if (childNotifications.isEmpty()) {
            // No children → remove summary
            cancelNotification(summaryId)
            return
        }

        // Children exist → ensure summary exists / is updated
        val summaryNotification = getAlarmGroupSummaryNotification(groupKey)
            ?: return

        postNotification(summaryId, summaryNotification)
    }


    private fun getActiveNotification(id: Int): StatusBarNotification? {
        return try {
            notificationManager.activeNotifications.find { it.id == id }
        } catch (_: Exception) {
            null
        }
    }

}
