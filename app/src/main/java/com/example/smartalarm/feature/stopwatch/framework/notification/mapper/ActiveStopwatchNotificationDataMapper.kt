package com.example.smartalarm.feature.stopwatch.framework.notification.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.notification.model.NotificationAction
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.home.presentation.view.HomeActivity.Companion.EXTRA_DESTINATION_ID
import com.example.smartalarm.feature.home.presentation.view.HomeActivity.Companion.EXTRA_NOTIFICATION_ACTION
import com.example.smartalarm.feature.home.presentation.view.HomeActivity.Companion.EXTRA_START_DESTINATION
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchKey
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.receivers.StopwatchReceiver
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationData
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationModel
import javax.inject.Inject

/**
 * Mapper that converts an [StopwatchNotificationModel.ActiveStopwatchModel] into a [StopwatchNotificationData]
 * suitable for displaying in a notification.
 *
 * This mapper is responsible for:
 * - Formatting the stopwatch elapsed time and lap count.
 * - Generating actionable buttons (pause, lap, resume, reset) depending on the stopwatch state.
 * - Creating the notification content intent to open the app.
 *
 * This class is injectable via Hilt.
 */
class ActiveStopwatchNotificationDataMapper @Inject constructor(
    private val timeFormatter: TimeFormatter,
    private val numberFormatter: NumberFormatter
) : AppNotificationDataMapper<StopwatchNotificationModel.ActiveStopwatchModel, StopwatchNotificationData> {

    /**
     * Maps an [StopwatchNotificationModel.ActiveStopwatchModel] to [StopwatchNotificationData].
     *
     * @param context Used to resolve string resources and create PendingIntents.
     * @param model The active stopwatch model containing timing and lap information.
     * @return A fully populated [StopwatchNotificationData] ready for notification display.
     */
    override fun map(
        context: Context,
        model: StopwatchNotificationModel.ActiveStopwatchModel
    ): StopwatchNotificationData {

        val stopwatch = model.stopwatch
        val lapCount = stopwatch.lapCount.takeIf { it > 0 } ?: 1
        val formattedElapsedTime = timeFormatter.formatDurationForStopwatch(durationMillis = stopwatch.elapsedTime, includeMillis = false)
        val contentText = context.getString(R.string.lap_time_format, numberFormatter.formatLocalizedNumber(lapCount.toLong(),true), formattedElapsedTime)

        val actions = buildActions(context, stopwatch)

        return StopwatchNotificationData(
            id = stopwatch.id,
            title = context.getString(R.string.stopwatch),
            contentText = contentText,
            actions = actions,
            contentIntent = buildStopwatchContentIntent(context, stopwatch),
            lapCount = lapCount,
            isRunning = stopwatch.isRunning,
            progress = stopwatch.getIndicatorProgress
        )
    }

    /**
     * Builds the list of notification actions depending on the stopwatch state.
     *
     * @param context Context used for resolving string resources and creating PendingIntents.
     * @param stopwatch The current [StopwatchModel].
     * @return A list of [NotificationAction] representing buttons in the notification.
     */
    private fun buildActions(context: Context, stopwatch: StopwatchModel): List<NotificationAction> {

        fun createAction(id: Int, titleRes: Int, iconRes: Int, action: String) = NotificationAction(
            id = id,
            title = context.getString(titleRes),
            icon = iconRes,
            pendingIntent = buildStopwatchPendingIntent(context, stopwatch.id, action)
        )

        return if (stopwatch.isRunning) {
            listOf(
                createAction(1, R.string.pause, R.drawable.ic_pause, StopWatchBroadCastAction.PAUSE),
                createAction(2, R.string.lap, R.drawable.ic_restart, StopWatchBroadCastAction.LAP)
            )
        } else {
            listOf(
                createAction(3, R.string.resume, R.drawable.ic_restart, StopWatchBroadCastAction.RESUME),
                createAction(4, R.string.reset, R.drawable.ic_restart, StopWatchBroadCastAction.RESET)
            )
        }
    }

    /**
     * Builds a [PendingIntent] for a broadcast action (pause, lap, resume, reset) tied to a stopwatch.
     *
     * @param context Context used to create the intent.
     * @param id The stopwatch ID.
     * @param action The broadcast action string.
     * @return A [PendingIntent] that can be attached to a notification action button.
     */
    private fun buildStopwatchPendingIntent(context: Context, id: Int, action: String): PendingIntent {
        val intent = Intent(context, StopwatchReceiver::class.java).apply {
            this.action = action
            putExtra(StopWatchKey.ID, id)
        }
        val requestCode = (id.toString() + action).hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Builds the [PendingIntent] for tapping the notification to open the app.
     *
     * @param context Context used to create the intent.
     * @param stopwatch The stopwatch associated with the notification.
     * @return A [PendingIntent] that opens [HomeActivity] with the stopwatch ID.
     */

    private fun buildStopwatchContentIntent(context: Context, stopwatch: StopwatchModel): PendingIntent {

        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ACTION, HomeActivity.ACTION_ACTIVE_STOPWATCH)
            putExtra(EXTRA_START_DESTINATION, R.id.stopwatchFragment)
            putExtra(EXTRA_DESTINATION_ID, stopwatch.id)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            stopwatch.id, // Unique ID per notification
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}

