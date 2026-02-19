package com.example.smartalarm.core.notification.manager

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.notification.factory.AppNotificationBuilderFactory
import com.example.smartalarm.core.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.notification.model.AppNotification
import com.example.smartalarm.core.notification.model.AppNotificationData
import com.example.smartalarm.core.notification.model.AppNotificationModel


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

    /**
     * Cancels an active notification by its ID.
     */
    protected fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
