package com.example.smartalarm.feature.timer.utility

import android.content.Context
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.extension.toLocalizedString


/**
 * Extension function for `Long` that converts a time in milliseconds to a formatted string.
 * The time is formatted as a localized string in the format "HH:mm:ss", where the digits
 * are localized to the current locale (e.g., Arabic numerals or Hindi numerals).
 *
 * @return A localized string representing the time in the format "HH:mm:ss", where
 * the digits are in the appropriate locale.
 */
fun Long.toFormattedTimerTime(context: Context): String {

    // Convert milliseconds to seconds
    val totalSec = this / 1000

    // Calculate hours, minutes, and seconds
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60

    // Convert hours, minutes, and seconds to localized strings
    val hoursLocalized = hours.toLocalizedString(true)
    val minutesLocalized = minutes.toLocalizedString(true)
    val secondsLocalized = seconds.toLocalizedString(true)

    // Return the formatted localized time using the context's string resources
    return context.getString(R.string.formatted_timer_time, hoursLocalized, minutesLocalized, secondsLocalized)
}


