package com.example.smartalarm.feature.timer.framework.notification.manager

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.framework.notification.manager.AppNotificationManager
import com.example.smartalarm.core.framework.notification.model.GroupableNotification
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationBuilderFactory
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationDataMapperFactory
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationGroup
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimerNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    val builderFactory: TimerNotificationBuilderFactory,
    val notificationManager: NotificationManagerCompat,
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
        postNotification(notificationId+300,notification)

        if (timerNotificationModel is GroupableNotification) {
            updateTimerGroupSummaryNotification(timerNotificationModel.groupKey, handledId = notificationId+300, isRemoving = false)
        }
    }

    fun cancelTimerNotification(notificationId : Int){
        val groupKey = getActiveNotification(notificationId+300)?.notification?.group
        cancelNotification(notificationId+300)

        if (groupKey != null) {
            // Tell the summary updater: "I just removed this ID, ignore it!"
            updateTimerGroupSummaryNotification(groupKey, handledId = notificationId+300, isRemoving = true)
        }
    }

    fun updateTimerNotification(notificationId : Int, notification: Notification) {
        postNotification(notificationId+300,notification)
    }


    // --- Private Helper Methods ---

    private fun getTimerGroupSummaryNotification(groupKey: String): Notification? {

        val  notificationGroup = TimerNotificationGroup.fromTimerGroupKey(groupKey)

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
    private fun updateTimerGroupSummaryNotification(groupKey: String, handledId: Int? = null, isRemoving: Boolean = false) {

        val active = notificationManager.activeNotifications.toList()
        val summaryId = groupKey.hashCode()

        // 1. Get current children (excluding summary)
        val childrenInTray = active.filter {
            it.notification.group == groupKey && it.id != summaryId
        }

        // 2. Calculate the REAL count
        // If we are removing, we subtract the handledId.
        // If we are posting, we ensure the handledId is counted even if the OS tray isn't updated yet.
        val finalChildrenIds = childrenInTray.map { it.id }.toMutableSet()

        if (isRemoving) {
            handledId?.let { finalChildrenIds.remove(it) }
        } else {
            handledId?.let { finalChildrenIds.add(it) }
        }

        when {

            // No children left -> Kill summary
            finalChildrenIds.isEmpty() -> {
                cancelNotification(summaryId)
            }

            // EXACTLY one child left -> Ungroup it so it doesn't vanish
            finalChildrenIds.size == 1 -> {

                val lastId = finalChildrenIds.first()
                val lastChildSbn = childrenInTray.find { it.id == lastId }

                if (lastChildSbn != null) {
                    promoteToStandalone(lastChildSbn)
                }

                cancelNotification(summaryId)
            }

            // 2 or more children -> Post/Update summary
            else -> {
                val summary = getTimerGroupSummaryNotification(groupKey) ?: return
                postNotification(summaryId, summary)
            }
        }
    }
    private fun promoteToStandalone(sbn: StatusBarNotification) {

        // This preserves the title, text, icon, and pending intents automatically
        val recoveredBuilder = NotificationCompat.Builder(context, sbn.notification)

        // 2. The "Promotion" Magic:
        // We explicitly set the group to null and groupSummary to false.
        // This tells Android: "Show this as a normal, standalone notification now."
        recoveredBuilder
            .setGroup(null)
            .setGroupSummary(false)


        // 3. Notify with the SAME ID
        // Using the same ID ensures we update the existing one rather than making a duplicate
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(sbn.id, recoveredBuilder.build())

    }



}