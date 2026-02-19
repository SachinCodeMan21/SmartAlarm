package com.example.smartalarm.core.utility.formatter.number


/**
 * Interface for formatting numbers based on the locale.
 *
 * This interface provides a method to format numbers in a localized manner, taking into account locale-specific numeral systems.
 * It also allows for leading zeros to be applied to numbers when necessary (e.g., 05 instead of 5).
 */
interface NumberFormatter {

    /**
     * Formats a number into a localized string representation.
     *
     * @param value The number to be formatted.
     * @param leadingZero Whether or not to include a leading zero for single-digit numbers (e.g., "05" instead of "5").
     * @return The formatted number as a string, adjusted for the current locale and with optional leading zeros.
     */
    fun formatLocalizedNumber(value: Long, leadingZero: Boolean): String
}