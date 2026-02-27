package com.example.smartalarm.feature.alarm.framework.notification.model

import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.channel.AppNotificationChannel


sealed class AlarmNotificationGroup(
    val channelId: String,
    val title: String,
    val defaultContent: String,
    val smallResIconId: Int = 0,
) {

    object Upcoming : AlarmNotificationGroup(
        channelId = AppNotificationChannel.UPCOMING_ALARM.channelId,
        title = "Upcoming Alarms",
        defaultContent = "You have scheduled alarms coming up",
        smallResIconId = R.drawable.ic_alarm
    )

    object Missed : AlarmNotificationGroup(
        channelId = AppNotificationChannel.MISSED_ALARM.channelId,
        title = "Missed Alarms",
        defaultContent = "Check your missed alarm alerts",
        smallResIconId = R.drawable.ic_alarm
    )

    object Snoozed : AlarmNotificationGroup(
        channelId = AppNotificationChannel.SNOOZED_ALARM.channelId,
        title = "Snoozed Alarms",
        defaultContent = "Alarms are currently paused",
        smallResIconId = R.drawable.ic_alarm
    )

    companion object {
        fun fromAlarmGroupKey(groupKey: String): AlarmNotificationGroup? =
            when (groupKey) {
                NotificationGroupKeys.UPCOMING_ALARMS -> Upcoming
                NotificationGroupKeys.MISSED_ALARMS -> Missed
                NotificationGroupKeys.SNOOZED_ALARMS -> Snoozed
                else -> null
            }
    }
}