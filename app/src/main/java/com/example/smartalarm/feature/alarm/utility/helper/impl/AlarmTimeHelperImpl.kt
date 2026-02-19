package com.example.smartalarm.feature.alarm.utility.helper.impl

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import java.time.LocalTime
import java.util.Calendar
import javax.inject.Inject

/**
 * Helper class for managing alarm time calculations, scheduling, and generating time-related summaries.
 *
 * This class provides utility methods for:
 * - Converting 12-hour time format (AM/PM) to 24-hour `LocalTime`.
 * - Calculating the next scheduled alarm trigger time based on the alarm's time and repeat days.
 * - Generating a human-readable string summarizing how much time is left until the next alarm.
 * - Handling snooze functionality and determining the exact snooze trigger time based on a snooze interval.
 *
 * It utilizes the `SystemClockHelper` to get the current system time and generate `ZonedDateTime` objects,
 * and `ResourceProvider` for retrieving localized strings used in time formatting.
 *
 * @constructor Inject constructor for dependency injection if needed.
 */
class AlarmTimeHelperImpl @Inject constructor(
    private val systemClockHelper: SystemClockHelper,
    private val numberFormatter: NumberFormatter,
    private val resourceProvider: ResourceProvider
) : AlarmTimeHelper {

    /**
     * Converts time from a 12-hour format (with AM/PM) to a 24-hour [java.time.LocalTime].
     *
     * This method takes the hour, minute, and AM/PM flag and returns the corresponding time
     * in the 24-hour format. The conversion handles edge cases such as 12 AM (converted to 00)
     * and 12 PM (remains 12), as well as adding 12 hours for PM times.
     *
     * @param hour The hour in 12-hour format (1–12).
     * @param min The minute of the hour (0–59).
     * @param amPm Integer flag indicating the time of day: 0 for AM, 1 for PM.
     * @return The corresponding [java.time.LocalTime] in 24-hour format.
     */
    override fun convertTo24HourTime(hour: Int, min: Int, amPm: Int): LocalTime {

        val isPm = amPm == 1

        val hour24 = when {
            hour == 12 && !isPm -> 0     // 12 AM -> 00 hours
            hour == 12 && isPm -> 12     // 12 PM -> 12 hours (no change)
            isPm -> hour + 12            // PM hours: add 12
            else -> hour                 // AM hours: no change
        }

        return LocalTime.of(hour24, min)
    }



    /**
     * Calculates the next scheduled alarm trigger time in epoch milliseconds based on the specified alarm time and repeat days.
     *
     * The behavior of the calculation depends on whether repeat days are provided:
     *
     * - **One-time alarm** (no repeat days): The alarm is scheduled for today if the specified alarm time hasn't passed for the current day.
     *   If the alarm time has already passed, it is scheduled for tomorrow.
     *
     * - **Recurring alarm** (with repeat days): The alarm is scheduled for the next occurrence based on the specified days of the week.
     *   The calculation considers the current day and time. If the alarm time has already passed for the current day in the repeat schedule,
     *   it will be scheduled for the next eligible day in the repeat schedule.
     *
     * The calculation uses the current system time (via `ZonedDateTime.now()`) to determine the next scheduled trigger time.
     *
     * The returned value is the exact point in time when the alarm will trigger, expressed as an epoch timestamp in milliseconds.
     *
     * @param alarmTime The local time of the alarm in 24-hour format (e.g., 08:00 for 8 AM).
     * @param repeatDays A set of [DayOfWeek] values indicating which days the alarm should repeat on (e.g., [MONDAY, WEDNESDAY]).
     *                   If this set is empty, the alarm is treated as a one-time alarm, and will trigger once at the specified time.
     * @return The epoch time in milliseconds representing the next scheduled alarm trigger time.
     *         This is the exact point in time when the alarm will go off.
     */
    override fun calculateNextAlarmTriggerMillis(alarmTime: LocalTime, repeatDays: Set<DayOfWeek>, ): Long {

        val now = systemClockHelper.getZonedDateTime()

        if (repeatDays.isEmpty()) {

            // Handle one-time alarm (no repeat days)
            val alarmForSchedule = now.with(alarmTime)
            return if (alarmForSchedule.isAfter(now)) {
                alarmForSchedule.toInstant().toEpochMilli()  // Scheduled for today
            } else {
                alarmForSchedule.plusDays(1).toInstant().toEpochMilli()  // Scheduled for tomorrow
            }
        }

        // Convert java.time.DayOfWeek → custom DayOfWeek enum (if necessary, based on your requirements)
        val today = DayOfWeek.valueOf(now.dayOfWeek.name.take(3))  // e.g., "MONDAY" → "MON"
        val nowTime = now.toLocalTime()

        // Find the number of days until the next scheduled alarm in repeatDays
        val daysUntilNextAlarm = repeatDays.minOfOrNull { day ->
            var daysDiff = (day.ordinal - today.ordinal + 7) % 7
            if (daysDiff == 0 && alarmTime.isBefore(nowTime)) {
                daysDiff = 7  // Skip today if the alarm time has already passed
            }
            daysDiff
        } ?: 0

        // Calculate the date and time of the next alarm
        val nextAlarmDate = now.toLocalDate().plusDays(daysUntilNextAlarm.toLong())
        val nextAlarmDateTime = systemClockHelper.createZonedDateTime(nextAlarmDate, alarmTime, now.zone)
        return nextAlarmDateTime.toInstant().toEpochMilli()
    }

    /**
     * Generates a human-readable string summarizing how much time is left until the next alarm rings.
     *
     * This method calculates the time remaining (in days, hours, and minutes) based on the provided `nextAlarmMillis`
     * (the epoch time of the next alarm) and formats it into a user-friendly message. The string also handles proper
     * pluralization for days, hours, and minutes. If the alarm time is in the past or immediate, a corresponding
     * message is returned.
     *
     * For example:
     * - "5 days, 3 hours, and 15 minutes"
     * - "2 hours and 45 minutes"
     * - "Less than a minute"
     * - "Alarm time is in the past or now"
     *
     * @param nextAlarmMillis The epoch milliseconds representing the time when the next alarm will trigger.
     * @return A formatted string describing the time remaining until the next alarm rings,
     *         or a message indicating if the alarm time is in the past or too soon.
     */
    override fun getFormattedTimeUntilNextAlarm(nextAlarmMillis: Long): String {

        val nowMillis = systemClockHelper.getCurrentTime()
        val diffMillis = nextAlarmMillis - nowMillis

        if (diffMillis <= 0) return resourceProvider.getString(R.string.alarm_time_is_in_the_past_or_now)

        val diffSeconds = diffMillis / 1000
        val days = diffSeconds / (24 * 3600)
        val hours = (diffSeconds % (24 * 3600)) / 3600
        val minutes = (diffSeconds % 3600) / 60

        // Helper function to pluralize time units dynamically based on value
        fun pluralize(value: String, singular: String, plural: String): String =
            "$value ${if (value.toLong() == 1L) singular else plural}"

        val parts = mutableListOf<String>()

        // Add day, hour, and minute parts if they are greater than zero
        if (days > 0) parts.add(pluralize(numberFormatter.formatLocalizedNumber(days,true), resourceProvider.getString(
            R.string.day), resourceProvider.getString(R.string.days)))
        if (hours > 0) parts.add(pluralize(numberFormatter.formatLocalizedNumber(hours,true), resourceProvider.getString(
            R.string.hour), resourceProvider.getString(R.string.hours)))
        if (minutes > 0) parts.add(pluralize(numberFormatter.formatLocalizedNumber(minutes,true), resourceProvider.getString(
            R.string.minute), resourceProvider.getString(R.string.minutes)))

        // If no time is left, return a default message indicating the alarm is less than a minute away
        if (parts.isEmpty()) return resourceProvider.getString(R.string.alarm_time_less_than_a_minute)

        // Construct the final result by joining parts with commas and "and"
        val result = when (parts.size) {
            1 -> parts[0]
            2 -> parts.joinToString(" " + resourceProvider.getString(R.string.and) + " ")
            else -> parts.dropLast(1).joinToString(", ") + ", " + resourceProvider.getString(R.string.and) + " " + parts.last()
        }

        return resourceProvider.getString(R.string.alarm_time_left, result)
    }


    /**
     * Calculates the next snooze time in milliseconds by adding the specified
     * number of minutes to the current time and rounding to the nearest minute.
     *
     * @param snoozeMinutes The number of minutes to add to the current time for the snooze.
     *                      For example, passing `10` will add 10 minutes to the current time.
     * @return The time in milliseconds representing the new time after the snooze interval.
     *         The returned time will have seconds and milliseconds set to `0`.
     */
    override fun getNextSnoozeMillis(snoozeMinutes: Int): Long {
        val now = Calendar.getInstance()
        now.add(Calendar.MINUTE, snoozeMinutes)  // Add snoozeMinutes to current time
        now.set(Calendar.SECOND, 0)  // Set seconds to 0
        now.set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
        return now.timeInMillis
    }

}