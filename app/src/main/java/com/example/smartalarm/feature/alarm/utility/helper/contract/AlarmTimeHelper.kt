package com.example.smartalarm.feature.alarm.utility.helper.contract

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * Interface for managing alarm time-related calculations and utility functions.
 *
 * This interface provides methods for:
 * - Converting 12-hour time format (AM/PM) to 24-hour `LocalTime`.
 * - Calculating the next scheduled alarm trigger time based on the alarm's time and repeat days.
 * - Generating a human-readable string summarizing how much time is left until the next alarm.
 * - Calculating the exact trigger time for a snooze alarm, based on a given snooze interval.
 *
 * Implementations of this interface are responsible for:
 * - Handling time conversions and calculations with respect to the system's current time.
 * - Providing time-related summaries in a format that can be used in notifications or UI.
 * - Supporting various alarm scheduling features, such as recurring alarms and snooze functionality.
 *
 * The methods in this interface are intended to be used by components that manage alarms,
 * such as alarm schedulers, notification managers, and user interfaces.
 *
 * @see LocalTime
 * @see ZonedDateTime
 */
interface AlarmTimeHelper {

    /**
     * Converts a 12-hour time format (with AM/PM) to a 24-hour [LocalTime].
     *
     * This method takes an hour, minute, and an AM/PM flag and converts them to a 24-hour
     * format. The conversion ensures correct handling of edge cases like 12 AM (converted to 00)
     * and 12 PM (remains 12), as well as adjusting for PM times.
     *
     * @param hour The hour in 12-hour format (1–12).
     * @param min The minute of the hour (0–59).
     * @param amPm An integer flag indicating whether the time is AM (0) or PM (1).
     * @return The corresponding [LocalTime] in 24-hour format.
     */
    fun convertTo24HourTime(hour: Int, min: Int, amPm: Int): LocalTime

    /**
     * Calculates the next scheduled alarm trigger time in epoch milliseconds.
     *
     * The behavior of this method depends on the provided alarm time and repeat days:
     * - If no repeat days are provided (one-time alarm), it calculates the next trigger time
     *   for today or tomorrow based on the current system time.
     * - If repeat days are provided (recurring alarm), it calculates the next trigger time
     *   based on the next eligible day in the repeat schedule.
     *
     * @param alarmTime The time of day for the alarm in 24-hour format (e.g., 08:00 for 8 AM).
     * @param repeatDays A set of [DayOfWeek] values indicating on which days the alarm should repeat.
     *                   If this set is empty, the alarm is treated as a one-time alarm.
     * @return The epoch time in milliseconds when the alarm is next scheduled to trigger.
     */
    fun calculateNextAlarmTriggerMillis(
        alarmTime: LocalTime,
        repeatDays: Set<DayOfWeek>
    ): Long

    /**
     * Generates a human-readable string representing how much time is left until the next alarm.
     *
     * This method calculates the time remaining (in days, hours, and minutes) based on the provided
     * `nextAlarmMillis` (the epoch time of the next alarm). It then formats the time difference
     * into a user-friendly string, handling pluralization for days, hours, and minutes, and returning
     * a message like "5 days, 3 hours, and 15 minutes" or "Less than a minute".
     *
     * @param nextAlarmMillis The epoch milliseconds representing the time when the next alarm is triggered.
     * @return A formatted string describing the time left until the next alarm rings (e.g., "2 hours and 30 minutes").
     */
    fun getFormattedTimeUntilNextAlarm(nextAlarmMillis: Long): String

    /**
     * Calculates the next snooze time in milliseconds by adding the specified
     * number of minutes to the current time and rounding to the nearest minute.
     *
     * @param snoozeMinutes The number of minutes to add to the current time for the snooze.
     *                      For example, passing `10` will add 10 minutes to the current time.
     * @return The time in milliseconds representing the new time after the snooze interval.
     *         The returned time will have seconds and milliseconds set to `0`.
     */
    fun getNextSnoozeMillis(snoozeMinutes: Int): Long
}
