package com.example.smartalarm.feature.timer.framework.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.jvm.java

/**
 * Implementation of [TimerScheduler] using Android's [AlarmManager].
 * * This class handles three distinct types of alarms for each timer:
 * 1. **Primary Timer:** Triggers when the countdown finishes.
 * 2. **Timeout:** A fallback trigger (10s later) to handle missed alarms or auto-dismissal.
 * 3. **Snooze:** A temporary alarm scheduled after a user postpones a ringing timer.
 * * Conflict prevention is managed by generating unique Request Codes for each
 * (TimerId + Action) combination.
 */
class TimerSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val systemClockHelper: SystemClockHelper
) : TimerScheduler {

    companion object {
        /** Delay added to the timer end time to trigger a timeout sequence. */
        private const val TIMEOUT_DELAY_MILLIS = 15L * 600000
    }

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Timer
    //---------------------------------------------------------------

    override fun scheduleTimer(timerId: Int, triggerAtMillis: Long) {
        schedule(
            timerId = timerId,
            triggerAtMillis = triggerAtMillis,
            action = TimerBroadCastAction.ACTION_START
        )
    }
    override fun cancelScheduledTimer(timerId: Int) {
        cancel(timerId, TimerBroadCastAction.ACTION_START)
    }



    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Timer Timeout
    //---------------------------------------------------------------

    override fun scheduleTimerTimeout(timerId: Int, triggerTimeInMillis: Long) {
        val timeoutTime = triggerTimeInMillis + TIMEOUT_DELAY_MILLIS
        schedule(
            timerId = timerId,
            triggerAtMillis = timeoutTime,
            action = TimerBroadCastAction.ACTION_TIMER_TIMEOUT
        )
    }

    override fun cancelTimerTimeout(timerId: Int) {
        cancel(timerId, TimerBroadCastAction.ACTION_TIMER_TIMEOUT)
    }



    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Timer
    //---------------------------------------------------------------

    override fun scheduleSnoozeTimer(timerId: Int, snoozeIntervalMillis: Long) {
        val triggerAt = System.currentTimeMillis() + snoozeIntervalMillis
        schedule(
            timerId = timerId,
            triggerAtMillis = triggerAt,
            action = TimerBroadCastAction.ACTION_SNOOZE
        )
    }

    override fun cancelSnoozeTimer(timerId: Int) {
        cancel(timerId, TimerBroadCastAction.ACTION_SNOOZE)
    }



    //---------------------------------------------------------------
    // 4] Global Cleanup
    //---------------------------------------------------------------

    override fun cancelAllScheduledTimers(timerId: Int) {
        cancelScheduledTimer(timerId)
        cancelTimerTimeout(timerId)
        cancelSnoozeTimer(timerId)
    }


    // Add this to your TimerScheduler interface and Implementation
    override fun scheduleServiceStart() {

        val startTimerMillis = systemClockHelper.getCurrentTime() + 1000L

        val intent = Intent(context, TimerReceiver::class.java).apply {
            this.action = TimerBroadCastAction.ACTION_START // Create this constant
        }

        val requestCode = TimerBroadCastAction.ACTION_START.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule exactly. This provides the FGS (Foreground Service) start exemption.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTimerMillis, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTimerMillis, pendingIntent)
        }
    }


    //---------------------------------------------------------------
    // Core Logic (Conflict Prevention Layer)
    //---------------------------------------------------------------

    /**
     * Schedules an exact alarm. Uses [AlarmManager.setExactAndAllowWhileIdle]
     * to ensure the timer fires even if the device is in Doze mode.
     */
    private fun schedule(timerId: Int, triggerAtMillis: Long, action: String) {
        val pendingIntent = createPendingIntent(timerId, action)

        // Check if we can actually schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback: This will fire within a window, but won't crash/fail silently
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            // High Precision
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels an existing alarm in the [AlarmManager] and invalidates
     * the [PendingIntent] locally.
     */
    private fun cancel(timerId: Int, action: String) {
        val pendingIntent = createPendingIntent(timerId, action)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * Creates a unique [PendingIntent] for a specific timer and action.
     * * **Conflict Resolution:** By using a unique requestCode generated from
     * both the timerId and the action string, Android treats these as separate
     * alarms, allowing a single timer to have a 'Start' and 'Timeout' alarm
     * registered simultaneously.
     */
    private fun createPendingIntent(timerId: Int, action: String): PendingIntent {
        val intent = Intent(context, TimerReceiver::class.java).apply {
            this.action = action
            putExtra(TimerKeys.TIMER_ID, timerId)
        }

        val requestCode = (timerId.toString() + action).hashCode()

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}