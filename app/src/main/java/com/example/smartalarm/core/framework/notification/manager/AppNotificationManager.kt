package com.example.smartalarm.core.framework.notification.manager

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


/**
 * Abstract base class for providing core notification infrastructure.
 * * This class handles low-level system interactions like permission checks,
 * posting, and cancelling, leaving the mapping and building logic to
 * specialized feature managers.
 */
abstract class AppNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat
) {

    /**
     * Posts a notification to the system tray if permissions are granted.
     */
    protected fun postNotification(notificationId: Int, notification: Notification) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(notificationId, notification)
    }

    protected fun postGroupNotification(notificationId: Int, notification: Notification, groupKey: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(notificationId, notification)
        updateGroupSummary(groupKey, notificationId, isRemoving = false)
    }

    /**
     * Cancels an active notification by its ID.
     */
    protected fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    protected fun cancelGroupedNotification(notificationId: Int, groupKey: String) {
        notificationManager.cancel(notificationId)
        updateGroupSummary(groupKey, notificationId, isRemoving = true)
    }


    /**
     * Optional: Override this ONLY if the feature supports notification grouping.
     * If this returns null, grouping logic is bypassed for this manager.
     */
    protected open fun getGroupSummaryNotification(groupKey: String): Notification? = null

    private fun updateGroupSummary(groupKey: String, handledId: Int, isRemoving: Boolean) {
        // If the feature manager doesn't provide a summary, we don't manage groups here
        val summaryNotification = getGroupSummaryNotification(groupKey) ?: return

        val active = notificationManager.activeNotifications.toList()
        val summaryId = groupKey.hashCode()

        // 1. Get current children (excluding the summary itself)
        val childrenInTray = active.filter {
            it.notification.group == groupKey && it.id != summaryId
        }

        // 2. Calculate the final state of IDs
        val finalChildrenIds = childrenInTray.map { it.id }.toMutableSet()
        if (isRemoving) finalChildrenIds.remove(handledId) else finalChildrenIds.add(handledId)

        when {
            // Case A: No children left -> Kill the summary
            finalChildrenIds.isEmpty() -> notificationManager.cancel(summaryId)

            // Case B: Exactly 1 child left -> Ungroup it so it stays visible as a single item
            finalChildrenIds.size == 1 -> {
                val lastId = finalChildrenIds.first()
                childrenInTray.find { it.id == lastId }?.let { promoteToStandalone(it) }
                notificationManager.cancel(summaryId)
            }

            // Case C: Multiple children -> Update/Post the summary
            else -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(summaryId, summaryNotification)
                }
            }
        }
    }

    private fun promoteToStandalone(sbn: StatusBarNotification) {
        val recoveredBuilder = NotificationCompat.Builder(context, sbn.notification)
            .setGroup(null)
            .setGroupSummary(false)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(sbn.id, recoveredBuilder.build())
        }
    }

    protected open fun getActiveNotification(id: Int): StatusBarNotification? {
        return try { notificationManager.activeNotifications.find { it.id == id } } catch (_: Exception) { null }
    }


}
