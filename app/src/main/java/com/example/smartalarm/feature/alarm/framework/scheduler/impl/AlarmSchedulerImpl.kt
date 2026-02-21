package com.example.smartalarm.feature.alarm.framework.scheduler.impl

import android.app.AlarmManager
import android.app.PendingIntent
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.scheduler.factory.AlarmIntentFactory
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManager: AlarmManager,
    private val alarmIntentFactory: AlarmIntentFactory,
    private val systemClockHelper: SystemClockHelper,
) : AlarmScheduler {

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Smart Alarm Methods
    //---------------------------------------------------------------

    override fun scheduleSmartAlarm(alarmId: Int, alarmTriggerTimeInMillis: Long) {
        val pendingIntent = alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        scheduleSetAlarmClock(alarmTriggerTimeInMillis, pendingIntent)
    }

    override fun cancelSmartAlarm(alarmId: Int) {
        cancel(alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId))
    }

    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Smart Alarm Timeout Methods
    //---------------------------------------------------------------

    override fun scheduleSmartAlarmTimeout(alarmId: Int, timeoutAt: Long) {
        val triggerTime = systemClockHelper.getCurrentTime() + timeoutAt
        val pendingIntent = alarmIntentFactory.createTimeoutAlarmPendingIntent(alarmId)

        // Timeout is usually a system-cleanup action, but if you want
        // it to be guaranteed even in Doze, setAlarmClock works:
        scheduleSetAlarmClock(triggerTime, pendingIntent)
    }

    override fun cancelSmartAlarmTimeout(alarmId: Int) {
        cancel(alarmIntentFactory.createTimeoutAlarmPendingIntent(alarmId))
    }

    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Alarm Methods
    //---------------------------------------------------------------

    override fun scheduleSnoozeAlarm(alarmId: Int, snoozeIntervalInMillis: Long) {
        val pendingIntent = alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        scheduleSetAlarmClock(snoozeIntervalInMillis, pendingIntent)
    }

    override fun cancelSnoozeAlarm(alarmId: Int) {
        cancel(alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId))
    }

    override fun cancelAllScheduledAlarms(alarmId: Int) {
        cancelSmartAlarm(alarmId)
        cancelSnoozeAlarm(alarmId)
        cancelSmartAlarmTimeout(alarmId)
    }

    //---------------------------------------------------------------
    // Core Logic Helpers
    //---------------------------------------------------------------

    private fun scheduleSetAlarmClock(triggerTime: Long, operation: PendingIntent) {
        // AlarmClockInfo takes the trigger time and a PendingIntent that
        // would show the alarm details if the user clicks the clock icon in the UI.
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, operation)
        alarmManager.setAlarmClock(alarmClockInfo, operation)
    }

    private fun cancel(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}