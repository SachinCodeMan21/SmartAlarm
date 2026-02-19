package com.example.smartalarm.feature.alarm.framework.scheduler.contract


/**
 * Interface defining methods for scheduling and canceling various types of alarms.
 *
 * This interface handles scheduling of different types of alarms including smart alarms, snooze alarms,
 * and alarm timeouts. It allows the management of these alarms by scheduling them at specific times
 * and canceling them when needed.
 */
interface AlarmScheduler {

    /**
     * Schedules a smart alarm that will trigger at the specified time.
     *
     * @param alarmId The ID of the alarm to be scheduled.
     * @param alarmTriggerTimeInMillis The time in milliseconds when the alarm should trigger.
     */
    fun scheduleSmartAlarm(alarmId: Int, alarmTriggerTimeInMillis: Long)

    /**
     * Cancels the scheduled smart alarm with the given alarm ID.
     *
     * @param alarmId The ID of the alarm to be canceled.
     */
    fun cancelSmartAlarm(alarmId: Int)

    /**
     * Schedules a timeout for a smart alarm.
     *
     * @param alarmId The ID of the alarm to be scheduled.
     * @param timeoutAt The time in milliseconds when the alarm timeout should occur.
     */
    fun scheduleSmartAlarmTimeout(alarmId: Int, timeoutAt: Long)

    /**
     * Cancels the timeout for the smart alarm with the given alarm ID.
     *
     * @param alarmId The ID of the alarm whose timeout should be canceled.
     */
    fun cancelSmartAlarmTimeout(alarmId: Int)

    /**
     * Schedules a snooze alarm to trigger after the specified snooze interval.
     *
     * @param alarmId The ID of the alarm to be snoozed.
     * @param snoozeIntervalInMillis The time in milliseconds after which the snooze alarm should trigger.
     */
    fun scheduleSnoozeAlarm(alarmId: Int, snoozeIntervalInMillis: Long)

    /**
     * Cancels the snooze alarm with the given alarm ID.
     *
     * @param alarmId The ID of the alarm to be canceled.
     */
    fun cancelSnoozeAlarm(alarmId: Int)

    /**
     * Cancels all types of alarms (smart alarm, snooze alarm, and smart alarm timeout) with the given alarm ID.
     *
     * @param alarmId The ID of the alarm whose associated alarms should be canceled.
     */
    fun cancelAllScheduledAlarms(alarmId: Int)
}