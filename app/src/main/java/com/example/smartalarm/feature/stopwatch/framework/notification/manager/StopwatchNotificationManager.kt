package com.example.smartalarm.feature.stopwatch.framework.notification.manager

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.smartalarm.core.framework.notification.manager.AppNotificationManager
import com.example.smartalarm.feature.stopwatch.framework.notification.factory.StopwatchNotificationBuilderFactory
import com.example.smartalarm.feature.stopwatch.framework.notification.factory.StopwatchNotificationDataMapperFactory
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Manager responsible for the lifecycle and orchestration of stopwatch-related notifications.
 *
 * This class serves as a high-level abstraction over the Android Notification system, specifically
 * tailored for the high-frequency updates required by a stopwatch (timer progress, lap updates,
 * and state transitions).
 *
 * ### Orchestration Flow:
 * 1. **Selection:** Uses [StopwatchNotificationDataMapperFactory] to select the strategy for
 * the current stopwatch state (e.g., Running vs. Paused).
 * 2. **Transformation:** Converts the raw [StopwatchNotificationModel] into localized UI data
 * (strings, icons, chronometer settings).
 * 3. **Construction:** Delegates to [StopwatchNotificationBuilderFactory] to generate the
 * final [Notification] object with appropriate actions (Start, Pause, Reset).
 * 4. **Delivery:** Posts the notification to the system tray via [NotificationManagerCompat].
 *
 * ### Design Patterns:
 * - **Strategy Pattern:** Dynamically selects mappers based on state.
 * - **Factory Pattern:** Decouples notification building and data mapping from the manager logic.
 *
 * @property context The application context used for resource retrieval and intent creation.
 * @property builderFactory Factory used to construct the actual [Notification] objects.
 * @property notificationManager The system service wrapper used to post or cancel notifications.
 * @property mapperFactory Factory that provides specific mappers based on the stopwatch state.
 */
class StopwatchNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    val builderFactory: StopwatchNotificationBuilderFactory,
    val notificationManager: NotificationManagerCompat,
    val mapperFactory: StopwatchNotificationDataMapperFactory
) : AppNotificationManager(context, notificationManager) {

    /**
     * Constructs a [Notification] object based on the provided [StopwatchNotificationModel].
     * * This handles the full transformation pipeline from domain model to a system-ready
     * notification, ensuring that the correct layout and actions are applied for the
     * current state.
     *
     * @param model The current state and metadata of the stopwatch.
     * @return A fully constructed [Notification] ready to be displayed.
     */
    fun getStopwatchNotification(model: StopwatchNotificationModel): Notification {
        val mapper = mapperFactory.getMapper(model.getMapperKey())
        val data = mapper.map(context, model)
        val notificationType = model.toNotification(data)
        return builderFactory.buildNotification(notificationType)
    }

    /**
     * Generates and immediately posts a stopwatch notification to the system tray.
     *
     * Use this method for both the initial notification post and subsequent updates
     * (e.g., when the stopwatch is paused or a new lap is added).
     *
     * @param notificationId The unique identifier for this notification (usually a constant).
     * @param model The updated stopwatch data to display.
     */
    fun updateStopwatchNotification(notificationId: Int, model: StopwatchNotificationModel) {
        val stopwatchNotification = getStopwatchNotification(model)
        postNotification(notificationId, stopwatchNotification)
    }
}