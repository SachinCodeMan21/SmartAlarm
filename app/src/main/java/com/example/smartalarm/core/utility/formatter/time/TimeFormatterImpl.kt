package com.example.smartalarm.core.utility.formatter.time

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.extension.toLocalizedString
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import java.util.Calendar
import javax.inject.Inject
import kotlin.text.padStart

/**
 * Implementation of the [TimeFormatter] interface that provides functionality to format
 * time-related values, such as durations for timers and stopwatches.
 *
 * This class uses a [NumberFormatter] to localize the number formatting (with options for leading zeros)
 * and a [ResourceProvider] to retrieve the localized string resources required for formatting the time.
 */
class TimeFormatterImpl @Inject constructor(
    private val resourceProvider: ResourceProvider, // Provides access to localized string resources
    private val numberFormatter: NumberFormatter // Formats numbers based on the current locale
) : TimeFormatter {


    //----------------
    // Alarm Methods
    //----------------
    override fun formatToAlarmTime(hour: Int, minute: Int): String {

        val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        // Get localized AM/PM string based on the hour
        val amPmString = if (hour < 12) {
            resourceProvider.getString(R.string.am)
        } else {
            resourceProvider.getString(R.string.pm)
        }

        // Get the hour in a two-digit format (e.g., "01" for 1 AM)
        val localizedHour = numberFormatter.formatLocalizedNumber(formattedHour.toLong(), true)

        // Get the minute in a two-digit format (e.g., "03" for 3 minutes)
        val localizedMinute = numberFormatter.formatLocalizedNumber(minute.toLong(), true)

        // Return the formatted time string, combining the localized hour, minute, and AM/PM string
        return "$localizedHour:$localizedMinute $amPmString"
    }

    override fun getFormattedDayAndTime(hour: Int, minute: Int): String {

        // Get the current day of the week as an integer (0 = Sunday, 6 = Saturday)
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        // Get the day name from the string-array resource using the day index
        val dayOfWeek = resourceProvider.getStringArray(R.array.full_weekdays)[currentDayOfWeek - 1]

        // Extract the first 3 letters of the day name
        val shortDay = dayOfWeek.take(3)

        // Format the time using the existing formatToAlarmTime method
        val formattedTime = formatToAlarmTime(hour, minute)

        // Combine the short day with the formatted time (e.g., "Sun, 08:00 AM")
        return "$shortDay, $formattedTime"
    }

    override fun getFormattedDayAndTime(alarmMillis: Long): String {

        val now = Calendar.getInstance()
        val alarmCal = Calendar.getInstance().apply {
            timeInMillis = alarmMillis
        }

        val dayLabel = when {
            isSameDay(now, alarmCal) ->
                resourceProvider.getString(R.string.today)

            isTomorrow(now, alarmCal) ->
                resourceProvider.getString(R.string.tomorrow)

            else -> {
                val dayIndex = alarmCal.get(Calendar.DAY_OF_WEEK) - 1
                val day = resourceProvider
                    .getStringArray(R.array.full_weekdays)[dayIndex]
                day.take(3)
            }
        }

        val hour = alarmCal.get(Calendar.HOUR_OF_DAY)
        val minute = alarmCal.get(Calendar.MINUTE)

        val formattedTime = formatToAlarmTime(hour, minute)

        return "$dayLabel, $formattedTime"
    }

    private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isTomorrow(today: Calendar, target: Calendar): Boolean {
        val tomorrow = today.clone() as Calendar
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        return isSameDay(tomorrow, target)
    }



    //----------------------
    // Time Formatting
    //----------------------

    /**
     * Formats the given time in "hh:mm a" format.
     * Example: 02:30 PM
     */
    override fun formatClockTime(timeInMillis: Long): String {

        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val amPmString = if (hour < 12) {
            resourceProvider.getString(R.string.am)
        } else {
            resourceProvider.getString(R.string.pm)
        }

        val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val localizedHour = numberFormatter.formatLocalizedNumber(formattedHour.toLong(), true)
        val localizedMinute = numberFormatter.formatLocalizedNumber(minute.toLong(), true)

        return "$localizedHour:$localizedMinute $amPmString"
    }

    /**
     * Formats the given date in "dd MMM yyyy" format.
     * Example: 14 Oct 2022
     */
    override fun formatDayMonth(dateInMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }

        // Localize the day of the month (with optional leading zero formatting)
        val day = numberFormatter.formatLocalizedNumber(calendar.get(Calendar.DAY_OF_MONTH).toLong(), false)

        // Localize the month using the array of month names
        val monthIndex = calendar.get(Calendar.MONTH) // Get the month index (0-11)
        val month = resourceProvider.getStringArray(R.array.month_names)[monthIndex]

        // Localize the year using the number formatter
        val year = numberFormatter.formatLocalizedNumber(calendar.get(Calendar.YEAR).toLong(), false)

        // Localize the day of the week (e.g., "Monday", "Tuesday")
        val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1  // Adjust for zero-indexed array
        val dayOfWeek = resourceProvider.getStringArray(R.array.full_weekdays)[dayOfWeekIndex]

        // Return the formatted date with localized day, month, and year
        return "$dayOfWeek, $day $month $year"
    }



    //----------------
    // Timer Methods
    //----------------

    override fun formatStringDigitsToTimerTextFormat(input: String): String {

        // Check if the input is empty, and return the default timer time string if it is
        if (input.isEmpty()) {
            return resourceProvider.getString(R.string.default_timer_time)
        }

        // Pad the string to ensure it has at least 6 characters (e.g., "000123" for 00:01:23)
        val padded = input.padStart(6, '0')

        // Extract hours, minutes, and seconds from the string and convert them to localized format
        val hours = numberFormatter.formatLocalizedNumber(padded.substring(0, 2).toLong(), true)
        val minutes = numberFormatter.formatLocalizedNumber(padded.substring(2, 4).toLong(), true)
        val seconds = numberFormatter.formatLocalizedNumber(padded.substring(4, 6).toLong(), true)

        // Return the formatted string using the provided format resource
        return resourceProvider.getString(R.string.formatted_timer_time, hours, minutes, seconds)
    }

    override fun formatStringDigitsToMillis(input: String): Long {

        // Pad the input string to ensure it has at least 6 characters (e.g., "000123" for 00:01:23)
        val paddedInput = input.padStart(6, '0')

        // Extract hours, minutes, and seconds from the string and convert them to integers
        val hours = paddedInput.substring(0, 2).toInt()
        val minutes = paddedInput.substring(2, 4).toInt()
        val seconds = paddedInput.substring(4, 6).toInt()

        // Convert hours, minutes, and seconds to milliseconds
        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    override fun formatMillisToTimerTextFormat(timerTimeMillis : Long) : String {

        // Convert milliseconds to seconds
        val totalSec = timerTimeMillis / 1000

        // Calculate hours, minutes, and seconds
        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60

        // Convert hours, minutes, and seconds to localized strings
        val hoursLocalized = hours.toLocalizedString(true)
        val minutesLocalized = minutes.toLocalizedString(true)
        val secondsLocalized = seconds.toLocalizedString(true)

        // Return the formatted localized time using the context's string resources
        return resourceProvider.getString(R.string.formatted_timer_time, hoursLocalized, minutesLocalized, secondsLocalized)
    }



    //-------------------
    // Stopwatch Methods
    //-------------------




    override fun formatDurationForStopwatch(durationMillis: Long, includeMillis: Boolean): String {

        val hours = durationMillis / 3600000
        val minutes = (durationMillis % 3600000) / 60000
        val seconds = (durationMillis % 60000) / 1000
        val milliSeconds = (durationMillis % 1000) / 10

        val localizedHour = numberFormatter.formatLocalizedNumber(hours, true)
        val localizedMinute = numberFormatter.formatLocalizedNumber(minutes, true)
        val localizedSecond = numberFormatter.formatLocalizedNumber(seconds, true)
        val localizedMilliSeconds = numberFormatter.formatLocalizedNumber(milliSeconds, true)

        return when {
            includeMillis -> "$localizedHour:$localizedMinute:$localizedSecond:$localizedMilliSeconds"
            hours > 0 -> resourceProvider.getString(R.string.hour_formatted_stopwatch_time, localizedHour, localizedMinute, localizedSecond)
            minutes > 0 -> resourceProvider.getString(R.string.min_formatted_stopwatch_time, localizedMinute, localizedSecond)
            else -> resourceProvider.getString(R.string.sec_formatted_stopwatch_time, localizedSecond)
        }
    }

    override fun formatMillisForStopwatch(durationMillis: Long): String {
        return numberFormatter.formatLocalizedNumber((durationMillis % 1000) / 10, true)
    }

}
