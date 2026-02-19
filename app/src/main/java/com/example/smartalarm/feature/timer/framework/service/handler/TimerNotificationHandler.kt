package com.example.smartalarm.feature.timer.framework.service.handler

import android.app.Service
import com.example.smartalarm.feature.timer.domain.model.TimerModel


interface TimerNotificationHandler {

    /**
     * Displays or updates the normal (non-foreground) timer notification.
     *
     * @param timers Full list of active timers to display.
     */
    fun showNormalTimerNotification(timers: List<TimerModel>)

    fun showMissedTimerNotification(timer: TimerModel)

    /**
     * Displays a foreground notification to keep the service alive.
     *
     * @param service The foreground service instance.
     * @param notificationId The notification ID.
     * @param timers Full list of timers (active and/or completed).
     */
    fun showForegroundTimerNotification(
        service: Service,
        notificationId: Int,
        timers: List<TimerModel>
    )

    /**
     * Updates an existing notification with the latest timer data.
     *
     * @param notificationId The ID of the notification to update.
     * @param timers Full updated timer list.
     */
    fun updateTimerNotification(
        notificationId: Int,
        timers: List<TimerModel>
    )

    /**
     * Removes the normal (non-foreground) timer notification.
     */
    fun removeNormalTimerNotification()
}

//
///**
// * Interface responsible for handling all notification-related operations
// * for the foreground timer service.
// *
// * This includes showing, updating, and removing both foreground and normal
// * timer notifications based on the current state of timers.
// */
//interface TimerNotificationHandler {
//
//    /**
//     * Displays or updates the normal (non-foreground) timer notification.
//     * Typically used for active timers while the service is already running in the foreground
//     * due to completed timers.
//     *
//     * @param timers The list of active timers to display in the notification.
//     */
//    fun showNormalTimerNotification(timers: List<TimerModel>)
//
//    /**
//     * Displays a foreground notification to keep the service alive.
//     * This is typically required when completed timers are present.
//     *
//     * @param service The [Service] instance associated with the foreground service.
//     * @param timers The list of timers (either active or completed) shown in the notification.
//     */
//    fun showForegroundTimerNotification(service: Service, notificationId: Int, timers: List<TimerModel>)
//
//    /**
//     * Updates an existing notification with the latest timer information.
//     *
//     * @param notificationId The ID of the notification to update.
//     * @param timers The list of timers whose updated data should be reflected.
//     */
//    fun updateTimerNotification(notificationId: Int, timers: List<TimerModel>)
//
//    /**
//     * Removes the normal timer notification (non-foreground).
//     * This is typically done when no active timers are running anymore.
//     */
//    fun removeNormalTimerNotification()
//}
