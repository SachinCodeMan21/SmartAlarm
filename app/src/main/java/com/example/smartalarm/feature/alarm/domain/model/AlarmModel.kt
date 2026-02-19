package com.example.smartalarm.feature.alarm.domain.model

import android.os.Parcelable
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

/**
 * Represents a complete alarm configuration within the application.
 *
 * This model encapsulates all relevant data needed to define, trigger, and manage an alarm.
 * It is typically stored in the database and used throughout the app for alarm scheduling,
 * editing, and notification purposes.
 *
 * @property id Unique identifier for the alarm (usually auto-generated).
 * @property label Optional label for the alarm (e.g., "Morning Wake Up").
 * @property time The time at which the alarm is set to trigger.
 * @property isDailyAlarm Flag indicating whether the alarm repeats every day.
 * @property days Set of specific [DayOfWeek]s on which the alarm should go off (used if not daily).
 * @property missions List of [Mission]s (e.g., math problems, tasks) required to dismiss the alarm.
 * @property volume Volume level of the alarm sound (range: 0 to 100).
 * @property isVibrateEnabled Indicates whether vibration is enabled along with the alarm sound.
 * @property alarmSound File path or URI for the alarm sound to be played.
 * @property snoozeSettings Configuration and state for snoozing the alarm (limit, count, interval, etc.).
 * @property isEnabled Indicates whether the alarm is currently active/enabled.
 * @property alarmState The current [AlarmState] of the alarm (e.g., UPCOMING, SNOOZED, RINGING).
 */
@Parcelize
data class AlarmModel(
    val id: Int = 0,
    val label: String = "",
    val time: LocalTime = LocalTime.of(0, 0),
    val isDailyAlarm: Boolean = false,
    val days: Set<DayOfWeek> = emptySet(),
    val missions: List<Mission> = emptyList(),
    val volume: Int = 70,
    val isVibrateEnabled: Boolean = true,
    val alarmSound: String = "",
    val snoozeSettings: SnoozeSettings = SnoozeSettings(),
    val isEnabled: Boolean = true,
    val alarmState: AlarmState = AlarmState.UPCOMING
) : Parcelable
