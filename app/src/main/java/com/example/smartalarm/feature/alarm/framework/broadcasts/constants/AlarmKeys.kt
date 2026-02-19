package com.example.smartalarm.feature.alarm.framework.broadcasts.constants

/**
 * Contains constant keys used for passing alarm-related data via [android.content.Intent] extras or [android.os.Bundle]s.
 *
 * These keys help ensure consistency when sending or receiving alarm data across components
 * such as Activities, Services, or BroadcastReceivers.
 */
object AlarmKeys {

    /**
     * Key for passing the alarm ID.
     *
     * This is used to uniquely identify an alarm instance when sending or receiving data
     * via `Intent.putExtra()` or `Bundle.putInt()`.
     */
    const val ALARM_ID = "com.example.smartalarm.ALARM_ID"
}
