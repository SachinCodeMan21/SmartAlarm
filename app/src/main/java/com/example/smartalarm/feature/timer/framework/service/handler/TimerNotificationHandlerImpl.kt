package com.example.smartalarm.feature.timer.framework.service.handler

import android.Manifest
import android.app.Notification
import android.app.Service
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerStatus
import com.example.smartalarm.feature.timer.framework.notification.manager.TimerNotificationManager
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationModel
import com.example.smartalarm.feature.timer.framework.service.ShowTimerService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerNotificationHandlerImpl @Inject constructor(
    private val timerNotificationManager: TimerNotificationManager,
) : TimerNotificationHandler {


    /**
     * Helper: Builds a [TimerNotificationModel] from the full timer list.
     * Computes running/paused/completed counts once.
     */
/*    private fun buildTimerNotificationModel(timers: List<TimerModel>): TimerNotificationModel {
        require(timers.isNotEmpty()) { "Timer list cannot be empty" }

        val representativeTimer = timers.first()

        val completedCount = timers.count { it.remainingTime <= 0 }
        val runningCount = timers.count { it.isTimerRunning && it.remainingTime > 0 && it.status == TimerStatus.RUNNING }
        val pausedCount = timers.count { (!it.isTimerRunning && it.remainingTime > 0) && it.status == TimerStatus.PAUSED }

        return if (completedCount > 0) {
            TimerNotificationModel.CompletedTimerModel(
                timer = representativeTimer,
                totalCount = timers.size,
                runningCount = runningCount,
                pausedCount = pausedCount,
                completedCount = completedCount
            )
        } else {
            TimerNotificationModel.ActiveTimerModel(
                timer = representativeTimer,
                totalCount = timers.size,
                runningCount = runningCount,
                pausedCount = pausedCount
            )
        }
    }*/
    private fun buildTimerNotificationModel(timers: List<TimerModel>): TimerNotificationModel {
        require(timers.isNotEmpty()) { "Timer list cannot be empty" }

        // Separate active and completed timers
        val completedTimers = timers.filter { it.remainingTime <= 0 }
        val activeTimers = timers.filter { it.remainingTime > 0 }

        // Logic for selecting the representative timer based on the state
        val representativeTimer = when {
            completedTimers.isNotEmpty() -> {
                // For completed timers, pick the one with the largest remaining time (farthest from zero)
                completedTimers.minByOrNull { it.remainingTime }
            }
            activeTimers.isNotEmpty() -> {
                // For active timers, pick the one with the smallest remaining time (closest to zero)
                activeTimers.minByOrNull { it.remainingTime }
            }
            else -> {
                // If there are no timers, return null or handle as needed
                null
            }
        }

        // Ensure that we have a representative timer selected
        requireNotNull(representativeTimer) { "No representative timer found" }

        // Count the timers in different states
        val completedCount = timers.count { it.remainingTime <= 0 }
        val runningCount = timers.count { it.isTimerRunning && it.remainingTime > 0 && it.status == TimerStatus.RUNNING }
        val pausedCount = timers.count { !it.isTimerRunning && it.remainingTime > 0 && it.status == TimerStatus.PAUSED }

        return if (completedCount > 0) {
            TimerNotificationModel.CompletedTimerModel(
                timer = representativeTimer,
                totalCount = timers.size,
                runningCount = runningCount,
                pausedCount = pausedCount,
                completedCount = completedCount
            )
        } else {
            TimerNotificationModel.ActiveTimerModel(
                timer = representativeTimer,
                totalCount = timers.size,
                runningCount = runningCount,
                pausedCount = pausedCount
            )
        }
    }


    /**
     * Builds a [Notification] from the full timer list.
     * Computes running/paused/completed counts once.
     */
    private fun buildNotification(timers: List<TimerModel>): Notification {
        val timerNotificationModel = buildTimerNotificationModel(timers)
        return timerNotificationManager.getTimerNotification(timerNotificationModel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showNormalTimerNotification(timers: List<TimerModel>) {
        if (timers.isEmpty()) return
        val timerNotificationModel = buildTimerNotificationModel(timers)
        timerNotificationManager.postTimerNotification(
            ShowTimerService.ACTIVE_NOTIFICATION_ID,
            timerNotificationModel
        )
    }

    /**
     * Show the missed alarm notification.
     * This will be triggered if a timer was missed and you need to inform the user.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showMissedTimerNotification(timer: TimerModel) {
        val missedAlarmNotificationModel = TimerNotificationModel.MissedTimerModel(timer)
        timerNotificationManager.postTimerNotification(timer.timerId,missedAlarmNotificationModel)
    }



    override fun showForegroundTimerNotification(
        service: Service,
        notificationId: Int,
        timers: List<TimerModel>
    ) {
        if (timers.isEmpty()) return
        service.startForeground(notificationId, buildNotification(timers))
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun updateTimerNotification(notificationId: Int, timers: List<TimerModel>) {
        if (timers.isEmpty()) return
        timerNotificationManager.updateTimerNotification(
            notificationId,
            buildNotification(timers)
        )
    }

    override fun removeNormalTimerNotification() {
        timerNotificationManager.cancelTimerNotification(
            ShowTimerService.ACTIVE_NOTIFICATION_ID
        )
    }
}