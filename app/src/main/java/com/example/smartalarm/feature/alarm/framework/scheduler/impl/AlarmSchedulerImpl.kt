package com.example.smartalarm.feature.alarm.framework.scheduler.impl

import android.app.AlarmManager
import android.os.Bundle
import com.example.smartalarm.core.model.TaskDestination
import com.example.smartalarm.core.schedular.AppAlarmScheduler
import com.example.smartalarm.core.schedular.CancelTask
import com.example.smartalarm.core.schedular.ScheduledTask
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.broadcasts.receivers.AlarmReceiver
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.scheduler.factory.AlarmIntentFactory
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val appAlarmScheduler: AppAlarmScheduler // Core scheduler injected
) : AlarmScheduler
{

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Smart Alarm
    //---------------------------------------------------------------

    override fun scheduleSmartAlarm(alarmId: Int, alarmTriggerTimeInMillis: Long) {
        val task = ScheduledTask(
            id = alarmId,
            timeInMillis = alarmTriggerTimeInMillis,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            extras = alarmExtras(alarmId),
            action = AlarmBroadCastAction.ACTION_TRIGGER
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelSmartAlarm(alarmId: Int) {
        val cancelTask = CancelTask(
            id = alarmId,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            action = AlarmBroadCastAction.ACTION_TRIGGER
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Smart Alarm Timeout
    //---------------------------------------------------------------

    override fun scheduleSmartAlarmTimeout(alarmId: Int, timeoutAt: Long) {
        val task = ScheduledTask(
            id = alarmId,
            timeInMillis = System.currentTimeMillis() + timeoutAt,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            extras = alarmExtras(alarmId),
            action = AlarmBroadCastAction.ACTION_TIMEOUT
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelSmartAlarmTimeout(alarmId: Int) {
        val cancelTask = CancelTask(
            id = alarmId,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            action = AlarmBroadCastAction.ACTION_TIMEOUT
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Alarm
    //---------------------------------------------------------------

    override fun scheduleSnoozeAlarm(alarmId: Int, snoozeIntervalInMillis: Long) {
        val task = ScheduledTask(
            id = alarmId,
            timeInMillis = System.currentTimeMillis() + snoozeIntervalInMillis,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            extras = alarmExtras(alarmId),
            action = AlarmBroadCastAction.ACTION_SNOOZE
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelSnoozeAlarm(alarmId: Int) {
        val cancelTask = CancelTask(
            id = alarmId,
            receiverClass = AlarmReceiver::class.java,
            destination = TaskDestination.ALARM_DISPLAY,
            action = AlarmBroadCastAction.ACTION_SNOOZE
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 4] Cancel All
    //---------------------------------------------------------------

    override fun cancelAllScheduledAlarms(alarmId: Int) {
        cancelSmartAlarm(alarmId)
        cancelSnoozeAlarm(alarmId)
        cancelSmartAlarmTimeout(alarmId)
    }

    private fun alarmExtras(alarmId: Int) =
        Bundle().apply { putInt(AlarmKeys.ALARM_ID, alarmId) }
}


/**
 * Implementation of [AlarmScheduler] that handles the scheduling and cancellation of smart alarms, snooze alarms, and alarm timeouts.
 *
 * This class interacts with the Android [AlarmManager] to schedule and cancel alarms at the specified times. It uses
 * an [AlarmIntentFactory] to generate the appropriate `PendingIntent` objects for each alarm, and a [SystemClockHelper]
 * to assist with time calculations.
 */
/*
class AlarmSchedulerImpl @Inject constructor(
    private val alarmManager: AlarmManager,
    private val alarmIntentFactory: AlarmIntentFactory,
    private val systemClockHelper: SystemClockHelper
) : AlarmScheduler
{

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Smart Alarm Methods
    //---------------------------------------------------------------

    */
/**
     * Schedules a smart alarm to trigger at the specified time.
     *
     * @param alarmId The ID of the alarm to be scheduled.
     * @param alarmTriggerTimeInMillis The time when the alarm should trigger in milliseconds.
     *//*

    override fun scheduleSmartAlarm(alarmId: Int, alarmTriggerTimeInMillis: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTriggerTimeInMillis,
            alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        )
    }

    */
/**
     * Cancels the scheduled smart alarm.
     *
     * @param alarmId The ID of the alarm to be canceled.
     *//*

    override fun cancelSmartAlarm(alarmId: Int) {
        val pendingIntent = alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Smart Alarm Timeout Methods
    //---------------------------------------------------------------

    */
/**
     * Schedules a timeout for the smart alarm to trigger after a specified timeout period.
     *
     * @param alarmId The ID of the alarm to be scheduled.
     * @param timeoutAt The time in milliseconds when the alarm timeout should occur.
     *//*

    override fun scheduleSmartAlarmTimeout(alarmId: Int, timeoutAt: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            systemClockHelper.getCurrentTime() + timeoutAt,
            alarmIntentFactory.createTimeoutAlarmPendingIntent(alarmId)
        )
    }

    */
/**
     * Cancels the timeout for the smart alarm.
     *
     * @param alarmId The ID of the alarm whose timeout should be canceled.
     *//*

    override fun cancelSmartAlarmTimeout(alarmId: Int) {
        val pendingIntent = alarmIntentFactory.createTimeoutAlarmPendingIntent(alarmId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Alarm Methods
    //---------------------------------------------------------------

    */
/**
     * Schedules a snooze alarm to trigger after the specified snooze interval.
     *
     * @param alarmId The ID of the alarm to be snoozed.
     * @param snoozeIntervalInMillis The time in milliseconds after which the snooze alarm should trigger.
     *//*

    override fun scheduleSnoozeAlarm(alarmId: Int, snoozeIntervalInMillis: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeIntervalInMillis,
            alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        )
    }

    */
/**
     * Cancels the snooze alarm.
     *
     * @param alarmId The ID of the alarm to be canceled.
     *//*

    override fun cancelSnoozeAlarm(alarmId: Int) {
        val pendingIntent = alarmIntentFactory.createTriggerAlarmPendingIntent(alarmId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    //-----------------------------------------------------------------------
    // Helper Method For Cancelling All Different Types Of Scheduled Alarms
    //-----------------------------------------------------------------------

    */
/**
     * Cancels all types of scheduled alarms (smart alarm, snooze alarm, and smart alarm timeout).
     *
     * @param alarmId The ID of the alarm whose associated alarms should be canceled.
     *//*

    override fun cancelAllScheduledAlarms(alarmId: Int) {
        cancelSmartAlarm(alarmId)
        cancelSnoozeAlarm(alarmId)
        cancelSmartAlarmTimeout(alarmId)
    }
}*/
