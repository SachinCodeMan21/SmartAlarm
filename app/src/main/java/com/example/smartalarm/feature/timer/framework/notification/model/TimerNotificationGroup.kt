package com.example.smartalarm.feature.timer.framework.notification.model

import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.channel.AppNotificationChannel
import com.example.smartalarm.feature.alarm.framework.notification.model.NotificationGroupKeys


sealed class TimerNotificationGroup(
    val channelId: String,
    val title: String,
    val defaultContent: String,
    val smallResIconId: Int = 0,
) {

    object MISSED : TimerNotificationGroup(
        channelId = AppNotificationChannel.MISSED_TIMER.channelId,
        title = "Missed Timers",
        defaultContent = "You have missed the scheduled timers",
        smallResIconId = R.drawable.ic_alarm
    )

    companion object {
        fun fromTimerGroupKey(groupKey: String): TimerNotificationGroup? =
            when (groupKey) {
                NotificationGroupKeys.MISSED_TIMERS -> MISSED
                else -> { null }
            }
    }

}