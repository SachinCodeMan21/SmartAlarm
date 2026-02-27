package com.example.smartalarm.core.framework.notification.channel

import android.app.NotificationManager
import androidx.annotation.StringRes
import com.example.smartalarm.R


/**
 * Enum representing the different notification channels used in the app.
 *
 * Each channel has an ID, name resource, description resource, and importance level.
 *
 * @property channelId The unique ID of the notification channel.
 * @property channelNameResId The string resource ID for the channel name.
 * @property channelDescResId The string resource ID for the channel description.
 * @property channelImportance The importance level of notifications posted to this channel.
 */
enum class AppNotificationChannel(
    val channelId: String,
    @field:StringRes val channelNameResId: Int,
    @field:StringRes val channelDescResId: Int,
    val channelImportance: Int
) {

    UPCOMING_ALARM(
        channelId = "upcoming_alarm_channel_id",
        channelNameResId = R.string.channel_upcoming_alarm,
        channelDescResId = R.string.desc_upcoming_alarm,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    ),

    SNOOZED_ALARM(
        channelId = "snoozed_alarm_channel_id",
        channelNameResId = R.string.channel_snoozed_alarm,
        channelDescResId = R.string.desc_snoozed_alarm,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    ),

    MISSED_ALARM(
        channelId = "missed_alarm_channel_id",
        channelNameResId = R.string.channel_missed_alarm,
        channelDescResId = R.string.desc_missed_alarm,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    ),

    SCHEDULED_ALARM(
        channelId = "scheduled_alarm_channel_id",
        channelNameResId = R.string.channel_scheduled_alarm,
        channelDescResId = R.string.desc_smart_alarm,
        channelImportance = NotificationManager.IMPORTANCE_HIGH
    ),

    ACTIVE_TIMER(
        channelId = "active_timer_channel_id",
        channelNameResId = R.string.channel_active_timer,
        channelDescResId = R.string.desc_active_timer,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    ),

    COMPLETED_TIMER(
        channelId = "completed_timer_channel_id",
        channelNameResId = R.string.channel_completed_timer,
        channelDescResId = R.string.desc_completed_timer,
        channelImportance = NotificationManager.IMPORTANCE_HIGH
    ),

    MISSED_TIMER(
        channelId = "missed_timer_channel_id",
        channelNameResId = R.string.channel_missed_timer,
        channelDescResId = R.string.desc_missed_timer,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    ),

    STOPWATCH(
        channelId = "stopwatch_channel_id",
        channelNameResId = R.string.channel_stopwatch,
        channelDescResId = R.string.desc_stopwatch,
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    );

}