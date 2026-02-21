package com.example.smartalarm.feature.alarm.framework.notification.model

import com.example.smartalarm.core.utility.Constants.PACKAGE


/**
 * Contains constant keys used to group notifications related to alarms.
 *
 * These keys are used as notification group identifiers to categorize
 * notifications for different alarm states, allowing better organization
 * and handling of notification stacks in the system notification shade.
 */
object NotificationGroupKeys {

    /** Group key for upcoming alarm notifications. */
    const val UPCOMING_ALARMS = "$PACKAGE.GROUP_UPCOMING_ALARMS"

    /** Group key for missed alarm notifications. */
    const val MISSED_ALARMS = "$PACKAGE.GROUP_MISSED_ALARMS"

    /** Group key for snoozed alarm notifications. */
    const val SNOOZED_ALARMS = "$PACKAGE.GROUP_SNOOZED_ALARMS"

    const val MISSED_TIMERS = "$PACKAGE.GROUP_MISSED_TIMERS"

}

