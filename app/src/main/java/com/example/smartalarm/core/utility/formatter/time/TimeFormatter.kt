package com.example.smartalarm.core.utility.formatter.time

/**
 * Interface that defines methods for formatting time-related values for both a timer and a stopwatch.
 *
 * This interface provides methods for converting and formatting time in various formats:
 * - Formatting time for a **timer** (hours, minutes, seconds).
 * - Converting time units (hours, minutes, seconds) into **milliseconds**.
 * - Formatting a **stopwatch duration** (milliseconds) into a human-readable string, with options to include milliseconds.
 * - Formatting the **milliseconds portion** of a stopwatch duration.
 *
 * Implementations of this interface should provide the actual logic for formatting and converting time based on the app's locale and user preferences.
 */
interface TimeFormatter {


    // Alarm Formatter Methods
    fun formatToAlarmTime(hour: Int, minute: Int): String

    fun getFormattedDayAndTime(hour: Int, minute: Int): String

    fun getFormattedDayAndTime(alarmMillis: Long): String


    // Timer Methods

    /**
     * Formats a given time string (in the format of `hh:mm:ss`) into a user-friendly, formatted string for display.
     * If the input is empty, it returns the default timer time string.
     *
     * The input string is expected to be 6 characters long, representing time in the `hh:mm:ss` format.
     * This method pads the input if necessary and formats the hours, minutes, and seconds using a localized number formatter.
     * It then returns a localized string that is ready for displaying in the UI.
     *
     * @param input A 6-character string representing the time in `hh:mm:ss` format (e.g., "001230" for 00:12:30).
     * @return A formatted string in the "hh:mm:ss" format, ready to be displayed to the user, or the default timer time if the input is empty.
     */
    fun formatStringDigitsToTimerTextFormat(input: String): String

    /**
     * Converts a given time string (in the format of `hh:mm:ss`) into the corresponding number of milliseconds.
     *
     * The input string is expected to be a 6-character string representing time in the `hh:mm:ss`format.
     * This method pads the input if necessary and converts the hours, minutes, and seconds into milliseconds.
     *
     * @param input A 6-character string representing the time in `hh:mm:ss`format (e.g., "001230" for 00:12:30).
     * @return The total time in milliseconds, calculated from the input string.
     */
    fun formatStringDigitsToMillis(input: String): Long

    /**
     * Converts a given time in milliseconds into a user-friendly, formatted string for display.
     * The time is broken down into hours, minutes, and seconds, and formatted into a localized time string.
     *
     * The method takes the input time in milliseconds, converts it into seconds, and then calculates the corresponding
     * hours, minutes, and seconds. These values are then localized and formatted as a string suitable for display.
     *
     * @param timerTimeMillis The time in milliseconds to be converted (e.g., 750000 for 12 minutes 30 seconds).
     * @return A localized, formatted string representing the time in the "hh:mm:ss" format, ready to be displayed.
     */
    fun formatMillisToTimerTextFormat(timerTimeMillis: Long): String


    fun formatClockTime(timeInMillis: Long): String

    fun formatDayMonth(dateInMillis: Long): String


    // Stopwatch Methods

    /**
     * Formats the given stopwatch duration (in milliseconds) into a localized string.
     *
     * The method converts the duration into hours, minutes, seconds, and optionally milliseconds.
     * It then formats these components using the app's locale and user preferences, and returns
     * a human-readable string suitable for displaying a stopwatch duration.
     *
     * @param durationMillis The duration in milliseconds to format.
     * @param includeMillis A flag indicating whether to include milliseconds in the formatted string.
     * @return A localized string representing the formatted stopwatch duration.
     */
    fun formatDurationForStopwatch(durationMillis: Long, includeMillis: Boolean = false): String

    /**
     * Formats the milliseconds portion of the given stopwatch duration (in milliseconds) into a localized string.
     *
     * This method extracts the milliseconds (or hundredths of a second) from the provided stopwatch duration
     * and formats it into a localized string.
     *
     * @param durationMillis The duration in milliseconds to format.
     * @return A localized string representing the milliseconds portion of the stopwatch duration.
     */
    fun formatMillisForStopwatch(durationMillis: Long): String
}
