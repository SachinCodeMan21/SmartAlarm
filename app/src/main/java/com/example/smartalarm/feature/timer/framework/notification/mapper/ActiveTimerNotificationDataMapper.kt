package com.example.smartalarm.feature.timer.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationData
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import javax.inject.Inject


class ActiveTimerNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter
) : AppNotificationDataMapper<
        TimerNotificationModel.ActiveTimerModel,
        TimerNotificationData
        > {

    override fun map(
        context: Context,
        model: TimerNotificationModel.ActiveTimerModel
    ): TimerNotificationData {

        val timer = model.timer
        val remainingTime =
            timeFormatter.formatMillisToTimerTextFormat(timer.remainingTime)

        val formattedBigText = buildBigText(model, remainingTime)
        val contentText = buildContentText(model, timer)
        val actions = buildActiveTimerActions(context, model)
        val contentIntent = buildContentIntent(context, timer)

        return TimerNotificationData(
            timer = timer,
            formattedBigText = formattedBigText,
            id = timer.timerId,
            title = context.getString(R.string.active_timer),
            contentText = contentText,
            actions = actions,
            contentIntent = contentIntent
        )
    }

    // -------------------------
    // Text Builders
    // -------------------------

    private fun buildBigText(
        model: TimerNotificationModel.ActiveTimerModel,
        remainingTime: String
    ): String {

        if (model.totalCount <= 1) {
            return "⏳ Time remaining: $remainingTime"
        }

        return buildString {
            append("⏳ Time remaining: $remainingTime")
            append("\n")
            append(" • ${model.runningCount} running")
            if (model.pausedCount > 0) {
                append("\n")
                append(" • ${model.pausedCount} paused")
            }
        }
    }

    private fun buildContentText(
        model: TimerNotificationModel.ActiveTimerModel,
        timer: TimerModel
    ): String {

        if (model.totalCount <= 1) {
            return "Timer #${timer.timerId}"
        }

        return buildString {
            append("${model.runningCount} running")
            if (model.pausedCount > 0) {
                append(" • ${model.pausedCount} paused")
            }
        }
    }

    // -------------------------
    // Actions
    // -------------------------

    private fun buildActiveTimerActions(
        context: Context,
        model: TimerNotificationModel.ActiveTimerModel
    ): List<NotificationAction> {

        val timer = model.timer

        // If multiple timers → show stop all
        if (model.totalCount > 1) {
            return listOf(
                NotificationAction(
                    id = generateRequestCode(
                        timer.timerId,
                        TimerBroadCastAction.ACTION_STOP_ALL_ACTIVE_TIMERS
                    ),
                    title = context.getString(R.string.stop_all_active),
                    icon = R.drawable.ic_delete,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_STOP_ALL_ACTIVE_TIMERS
                    )
                )
            )
        }

        // Single timer actions
        return if (timer.isTimerRunning) {
            listOf(
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_PAUSE),
                    title = context.getString(R.string.pause),
                    icon = R.drawable.ic_pause,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_PAUSE
                    )
                ),
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_SNOOZE),
                    title = context.getString(R.string.add_one_minute),
                    icon = R.drawable.ic_add,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_SNOOZE
                    )
                )
            )
        } else {
            listOf(
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_RESUME),
                    title = context.getString(R.string.resume),
                    icon = R.drawable.ic_restart,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_RESUME
                    )
                ),
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_STOP),
                    title = context.getString(R.string.stop),
                    icon = R.drawable.ic_delete,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_STOP
                    )
                )
            )
        }
    }

    // -------------------------
    // PendingIntents
    // -------------------------

    private fun createPendingIntent(
        context: Context,
        timer: TimerModel,
        action: String
    ): PendingIntent {

        val intent = Intent(context, TimerReceiver::class.java).apply {
            this.action = action
            putExtra(TimerKeys.TIMER_ID, timer.timerId)
        }

        return PendingIntent.getBroadcast(
            context,
            generateRequestCode(timer.timerId, action),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildContentIntent(
        context: Context,
        timer: TimerModel
    ): PendingIntent {
        // We only target HomeActivity. The "Runtime Decision" happens there.
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_NOTIFICATION_ACTION, HomeActivity.ACTION_TIMER_ACTIVE)
            putExtra(HomeActivity.EXTRA_START_DESTINATION, R.id.timerFragment)
            putExtra(HomeActivity.EXTRA_DESTINATION_ID, timer.timerId)

            // Use these flags to ensure we use the existing HomeActivity instance if it's open
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            timer.timerId, // Unique ID to prevent notification collisions
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun generateRequestCode(id: Int, action: String): Int =
        (id.toString() + action).hashCode()
}
