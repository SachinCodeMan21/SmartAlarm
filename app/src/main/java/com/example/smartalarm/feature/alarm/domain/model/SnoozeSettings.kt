package com.example.smartalarm.feature.alarm.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing the snooze configuration and state for an alarm.
 *
 * @property isSnoozeEnabled Indicates whether the snooze feature is enabled for the alarm.
 * @property isAlarmSnoozed Tracks if the alarm is currently in a snoozed state.
 * @property snoozedCount The number of times the alarm has been snoozed so far.
 * @property snoozeLimit The maximum number of times the alarm can be snoozed.
 * @property snoozeIntervalMinutes The duration (in minutes) for each snooze interval.
 *
 * Implements [Parcelable] to allow passing between Android components.
 */
@Parcelize
data class SnoozeSettings(
    val isSnoozeEnabled : Boolean = true,
    val isAlarmSnoozed: Boolean = false,
    val snoozeLimit: Int = 3,
    val snoozedCount: Int = 3,
    val snoozeIntervalMinutes: Int = 10
) : Parcelable
