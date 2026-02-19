package com.example.smartalarm.feature.stopwatch.framework.notification.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.channel.AppNotificationChannel
import com.example.smartalarm.core.notification.model.NotificationAction
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotification
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds the notification for an active stopwatch.
 *
 * This builder is responsible for constructing a foreground service notification
 * that displays stopwatch status, actions (e.g., pause, lap), and content based on
 * [StopwatchNotification.ActiveStopwatch] data.
 */
@Singleton
class ActiveStopwatchNotificationBuilder @Inject constructor() :
    AppNotificationBuilder<StopwatchNotification.ActiveStopwatch> {

    /**
     * Builds and returns a [Notification] for the active stopwatch state.
     *
     * @param context The application context used for resources and pending intents.
     * @param notificationType The notification type containing UI data for the stopwatch.
     * @return A fully constructed [Notification] instance.
     */
    override fun buildNotification(
        context: Context,
        notificationType: StopwatchNotification.ActiveStopwatch
    ): Notification {

        val data = notificationType.data

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.STOPWATCH.channelId)
            .setSmallIcon(R.drawable.ic_stop_watch)
            .setContentTitle(context.getString(R.string.stopwatch))
            .setContentText(data.contentText)
            .setContentIntent(data.contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)

        addNotificationActions(builder, data.actions)

        return builder.build()
    }

    /**
     * Adds custom actions (e.g., pause, lap, reset) to the notification.
     *
     * @param builder The [NotificationCompat.Builder] instance to modify.
     * @param actions A list of [NotificationAction] representing interactive actions.
     */
    private fun addNotificationActions(
        builder: NotificationCompat.Builder,
        actions: List<NotificationAction>
    ) {
        actions.forEach { action ->
            builder.addAction(
                action.icon ?: 0,
                action.title,
                action.pendingIntent
            )
        }
    }
}

