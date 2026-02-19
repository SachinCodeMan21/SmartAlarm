package com.example.smartalarm.feature.alarm.framework.notification.model


/**
 * Contains constant keys used to group notifications related to alarms.
 *
 * These keys are used as notification group identifiers to categorize
 * notifications for different alarm states, allowing better organization
 * and handling of notification stacks in the system notification shade.
 */
object NotificationGroupKeys {

    /** Group key for upcoming alarm notifications. */
    const val UPCOMING_ALARMS = "com.example.smartalarm.GROUP_UPCOMING_ALARMS"

    /** Group key for missed alarm notifications. */
    const val MISSED_ALARMS = "com.example.smartalarm.GROUP_MISSED_ALARMS"

    /** Group key for snoozed alarm notifications. */
    const val SNOOZED_ALARMS = "com.example.smartalarm.GROUP_SNOOZED_ALARMS"
}

