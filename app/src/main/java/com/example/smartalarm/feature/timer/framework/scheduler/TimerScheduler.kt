package com.example.smartalarm.feature.timer.framework.scheduler

import com.example.smartalarm.feature.timer.domain.model.TimerModel

/**
 * Handles scheduling timer completion alarms using AlarmManager.
 * Ensures timers can complete even when app is killed or device restarts.
 */
interface TimerScheduler {

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Timer
    //---------------------------------------------------------------

    fun scheduleTimer(timerId: Int, triggerAtMillis: Long)

    fun cancelScheduledTimer(timerId: Int)

    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Timer Timeout
    //---------------------------------------------------------------

    fun scheduleTimerTimeout(timerId: Int, triggerTimeInMillis: Long)

    fun cancelTimerTimeout(timerId: Int)

    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Timer
    //---------------------------------------------------------------

    fun scheduleSnoozeTimer(timerId: Int, snoozeIntervalMillis: Long)

    fun cancelSnoozeTimer(timerId: Int)

    //---------------------------------------------------------------
    // 4] Cancel All
    //---------------------------------------------------------------

    fun cancelAllScheduledTimers(timerId: Int)

    fun scheduleServiceStart()

}
