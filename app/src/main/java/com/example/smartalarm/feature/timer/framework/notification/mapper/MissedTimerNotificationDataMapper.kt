package com.example.smartalarm.feature.timer.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationData
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import javax.inject.Inject

class MissedTimerNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<TimerNotificationModel.MissedTimerModel, TimerNotificationData> {

    override fun map(
        context: Context,
        model: TimerNotificationModel.MissedTimerModel
    ): TimerNotificationData {

        // Format the target time using the timeFormatter
        val formattedTargetTime = timeFormatter.formatMillisToTimerTextFormat(model.timer.targetTime)

        val contentText = buildContentText(context, model)
        val contentIntent = buildContentIntent(context, model.timer)

        return TimerNotificationData(
            timer = model.timer,
            formattedBigText = context.getString(R.string.missed_timer_target_time, formattedTargetTime),
            id = model.timer.timerId,
            title = context.getString(R.string.missed_alarm),
            contentText = contentText,
            actions = emptyList(), // No actions for this notification
            contentIntent = contentIntent
        )
    }

    // Build content text (e.g., "Missed Timer Target Time: 02:30 PM")
    private fun buildContentText(
        context: Context,
        model: TimerNotificationModel.MissedTimerModel
    ): String {
        // Format the target time using the time formatter
        val formattedTargetTime = timeFormatter.formatMillisToTimerTextFormat(model.timer.targetTime)
        return context.getString(R.string.missed_timer_target_time, formattedTargetTime)
    }

    // Content intent that opens the HomeActivity or any other relevant activity
    private fun buildContentIntent(context: Context, timer: TimerModel): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_NOTIFICATION_ACTION, HomeActivity.ACTION_TIMER_MISSED)
            putExtra(HomeActivity.EXTRA_START_DESTINATION, R.id.timerFragment)
            putExtra(HomeActivity.EXTRA_DESTINATION_ID, timer.timerId)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            timer.timerId, // Unique ID to prevent notification collisions
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
