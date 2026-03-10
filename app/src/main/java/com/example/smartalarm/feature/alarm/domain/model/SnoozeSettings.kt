package com.example.smartalarm.feature.alarm.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the snooze configuration for an alarm.
 *
 * This class encapsulates all settings related to snoozing an alarm,
 * including whether snooze is enabled, how many times it has been used,
 * the maximum allowed snoozes, and the interval between snoozes.
 *
 * Implements [Parcelable] to allow easy passing between Android components.
 *
 * @property isSnoozeEnabled True if the snooze feature is enabled for the alarm.
 * @property isAlarmSnoozed True if the alarm is currently in a snoozed state.
 * @property snoozeLimit The maximum number of times the user is allowed to snooze the alarm.
 * @property snoozedCount The number of times the alarm has already been snoozed.
 * @property snoozeIntervalMinutes The duration of each snooze in minutes.
 */
@Parcelize
data class SnoozeSettings(
    val isSnoozeEnabled: Boolean = true,
    val isAlarmSnoozed: Boolean = false,
    val snoozeLimit: Int = 3,
    val snoozedCount: Int = 3,
    val snoozeIntervalMinutes: Int = 10
) : Parcelable