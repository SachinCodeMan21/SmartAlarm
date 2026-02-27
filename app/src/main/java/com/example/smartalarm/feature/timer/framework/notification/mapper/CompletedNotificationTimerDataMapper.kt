package com.example.smartalarm.feature.timer.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.framework.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationData
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import javax.inject.Inject


class CompletedNotificationTimerDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter,
) : AppNotificationDataMapper<
        TimerNotificationModel.CompletedTimerModel,
        TimerNotificationData
        > {


    // ✅ Create it ONCE on first access
    private var cachedFullScreenIntent: PendingIntent? = null


    override fun map(
        context: Context,
        model: TimerNotificationModel.CompletedTimerModel
    ): TimerNotificationData {

        val timer = model.timer
        val remainingTime = timeFormatter.formatMillisToTimerTextFormat(timer.remainingTime)

        val formattedBigText = buildBigText(context, model, remainingTime)
        val contentText = buildContentText(context, model, timer)
        val actions = buildCompletedTimerActions(context, model)
        // ✅ Create once, then reuse
        if (cachedFullScreenIntent == null) {
            cachedFullScreenIntent = buildContentIntent(context)
        }
        return TimerNotificationData(
            timer = timer,
            formattedBigText = formattedBigText,
            id = timer.timerId,
            title = context.getString(R.string.completed_timer),
            contentText = contentText,
            actions = actions,
            contentIntent = cachedFullScreenIntent
        )
    }

    // -------------------------
    // Text Builders
    // -------------------------

    private fun buildBigText(
        context: Context, // Add context parameter to access resources
        model: TimerNotificationModel.CompletedTimerModel,
        remainingTime: String
    ): String {

        if (model.totalCount <= 1) {
            return context.getString(R.string.time_after_completion, remainingTime)
        }

        return buildString {
            append(context.getString(R.string.time_after_completion, remainingTime))
            append("\n")
            append(context.getString(R.string.completed_timer_completed_count, model.completedCount))

            if (model.runningCount > 0) {
                append(" ")
                append(context.getString(R.string.timer_running_count, model.runningCount))
            }
            if (model.pausedCount > 0) {
                append(" ")
                append(context.getString(R.string.timer_paused_count, model.pausedCount))
            }
        }
    }

    private fun buildContentText(
        context: Context, // Add context parameter to access resources
        model: TimerNotificationModel.CompletedTimerModel,
        timer: TimerModel
    ): String {

        if (model.totalCount <= 1) {
            return context.getString(R.string.timer_label, timer.timerId)
        }

        return buildString {
            append(context.getString(R.string.completed_timer_completed_count, model.completedCount))
            if (model.runningCount > 0) {
                append(" ")
                append(context.getString(R.string.timer_running_count, model.runningCount))
            }
            if (model.pausedCount > 0) {
                append(" ")
                append(context.getString(R.string.timer_paused_count, model.pausedCount))
            }
        }
    }


    // -------------------------
    // Actions
    // -------------------------

    private fun buildCompletedTimerActions(
        context: Context,
        model: TimerNotificationModel.CompletedTimerModel
    ): List<NotificationAction> {

        val timer = model.timer

        // Multiple completed timers → show stop all
        if (model.totalCount > 1) {
            return listOf(
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_STOP_ALL_COMPLETED_TIMERS),
                    title = context.getString(R.string.stop_all_completed),
                    icon = R.drawable.ic_delete,
                    pendingIntent = createPendingIntent(
                        context,
                        timer,
                        TimerBroadCastAction.ACTION_STOP_ALL_COMPLETED_TIMERS
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
                    pendingIntent = createPendingIntent(context, timer, TimerBroadCastAction.ACTION_PAUSE)
                ),
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_SNOOZE),
                    title = context.getString(R.string.add_one_minute),
                    icon = R.drawable.ic_add,
                    pendingIntent = createPendingIntent(context, timer, TimerBroadCastAction.ACTION_SNOOZE)
                )
            )
        }
        else {
            listOf(
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_RESUME),
                    title = context.getString(R.string.resume),
                    icon = R.drawable.ic_restart,
                    pendingIntent = createPendingIntent(context, timer, TimerBroadCastAction.ACTION_RESUME)
                ),
                NotificationAction(
                    id = generateRequestCode(timer.timerId, TimerBroadCastAction.ACTION_STOP),
                    title = context.getString(R.string.stop),
                    icon = R.drawable.ic_delete,
                    pendingIntent = createPendingIntent(context, timer, TimerBroadCastAction.ACTION_STOP)
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


    private fun buildContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_NOTIFICATION_ACTION, HomeActivity.ACTION_TIMER_COMPLETED)
            putExtra(HomeActivity.EXTRA_START_DESTINATION, R.id.timerFragment)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            0,  // Static requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun generateRequestCode(id: Int, action: String): Int =
        (id.toString() + action).hashCode()

}