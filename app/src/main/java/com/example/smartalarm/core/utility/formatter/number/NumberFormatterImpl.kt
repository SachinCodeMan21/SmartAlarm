package com.example.smartalarm.core.utility.formatter.number

import android.icu.text.NumberFormat
import com.example.smartalarm.core.utility.provider.locale.LocaleProvider
import javax.inject.Inject


/**
 * Implementation of [NumberFormatter] that formats numbers based on the locale settings.
 *
 * This implementation uses [LocaleProvider] to retrieve the appropriate locale and adjusts the formatting accordingly.
 * It also allows adding leading zeros for single-digit numbers when specified.
 *
 * @param localeProvider A provider for locale adjustments based on the current locale.
 */
class NumberFormatterImpl @Inject constructor(
    private val localeProvider: LocaleProvider // Provider for locale adjustments
) : NumberFormatter {

    /**
     * Formats a given number into a localized string, optionally including a leading zero.
     *
     * This method adjusts the number formatting based on the system's locale and allows for leading zeros if requested.
     * It utilizes [LocaleProvider] to retrieve the correct locale for numeral system formatting.
     *
     * @param value The number to be formatted.
     * @param leadingZero Whether or not to add a leading zero for single-digit numbers (e.g., "05" instead of "5").
     * @return The formatted number as a string.
     */
    override fun formatLocalizedNumber(value: Long, leadingZero: Boolean): String {

        // Get the locale adjusted for the correct numeral system
        val correctedLocale = localeProvider.getLocaleWithCorrectNumeralsForDefaultLocale()

        // Create a NumberFormat instance using the adjusted locale
        val formatter = NumberFormat.getIntegerInstance(correctedLocale)

        // Set the minimum integer digits to 2 if leadingZero is true
        if (leadingZero) {
            formatter.minimumIntegerDigits = 2
        }

        // Format the number and return the result
        return formatter.format(value)
    }
}