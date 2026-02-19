package com.example.smartalarm.core.notification.model

import android.app.Notification

interface GroupableNotificationManager {
    fun showGroupSummary(notificationId: Int, notification: Notification)
    fun dismissGroupSummaryIfEmpty(notificationId: Int)
}