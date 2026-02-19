package com.example.smartalarm.feature.alarm.framework.broadcasts.constants

import com.example.smartalarm.core.utility.Constants.PACKAGE


/**
 * Contains constant action strings used for handling alarm-related broadcast intents.
 *
 * These actions are typically used with [android.content.BroadcastReceiver]s and [android.app.PendingIntent]s to
 * trigger, control, or manage alarms via system or app-level broadcasts.
 */
object AlarmBroadCastAction {

    /**
     * Broadcast action to trigger the alarm.
     *
     * Used to start the alarm when the scheduled time is reached.
     */
    const val ACTION_TRIGGER = "$PACKAGE.action.ALARM_TRIGGER"

    /**
     * Broadcast action to pause an ongoing alarm.
     *
     * Useful in scenarios like temporarily halting alarm during phone calls or focus modes.
     */
    const val ACTION_PAUSE = "$PACKAGE.action.ALARM_PAUSE "

    /**
     * Broadcast action to resume a previously paused alarm.
     *
     * Used to continue the alarm from the point it was paused.
     */
    const val ACTION_RESUME = "$PACKAGE.action.ALARM_RESUME"

    /**
     * Broadcast action to snooze the alarm.
     *
     * Triggers snooze logic and reschedules the alarm for a later time.
     */
    const val ACTION_SNOOZE = "$PACKAGE.action.ALARM_SNOOZE"

    /**
     * Broadcast action to retrigger the alarm.
     *
     * Can be used to restart the alarm if missed or dismissed prematurely.
     */
    const val ACTION_RETRIGGER = "$PACKAGE.action.ALARM_RETRIGGER"

    /**
     * Broadcast action to  timeout the alarm.
     *
     * Triggers alarm timeout logic & show missed notification & updates the alarm.
     */
    const val ACTION_TIMEOUT = "$PACKAGE.action.ALARM_TIMEOUT"

    /**
     * Broadcast action to dismiss the alarm.
     *
     * Used when the user has completed the alarm or cancels it intentionally.
     */
    const val ACTION_DISMISS = "$PACKAGE.action.ALARM_DISMISS"


    /**
     * Broadcast action to stop an alarm that is currently ringing.
     *
     * Usually used in response to user action from notifications or in-app controls.
     */
    const val ACTION_STOP = "$PACKAGE.action.ALARM_STOP"
}
