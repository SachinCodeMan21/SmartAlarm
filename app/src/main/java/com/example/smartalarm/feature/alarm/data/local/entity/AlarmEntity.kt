package com.example.smartalarm.feature.alarm.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import java.time.LocalTime

/**
 * Represents an alarm stored in the database.
 *
 * Maps to the `alarm_table` in the database.
 *
 * @property id The unique identifier for the alarm (auto-generated).
 * @property label Optional label or name of the alarm (e.g., "Morning Wake Up").
 * @property time The time at which the alarm is set to trigger.
 * @property isDailyAlarm Indicates if the alarm repeats daily.
 * @property days The set of days on which the alarm is active (e.g., MON, TUE).
 * @property volume The volume level for the alarm sound (0 to 100).
 * @property isVibrateEnabled Whether vibration is enabled for the alarm.
 * @property alarmSound The sound URI or identifier to play when the alarm triggers.
 * @property snoozeSettings Embedded snooze-related settings (enabled, interval, limits).
 * @property isEnabled Whether the alarm is currently active.
 * @property alarmState The current state of the alarm as a string (e.g., "RINGING", "SNOOZED").
 */
@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val time: LocalTime,
    val isDailyAlarm: Boolean,
    val days: Set<DayOfWeek>,
    val volume: Int,
    val isVibrateEnabled: Boolean,
    val alarmSound: String,
    @Embedded
    val snoozeSettings: SnoozeSettings,
    val isEnabled: Boolean,
    val alarmState: String
)
