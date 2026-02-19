package com.example.smartalarm.core.utility.provider.locale

import android.icu.util.ULocale

/**
 * Interface for providing locale-related functionality, specifically for retrieving
 * the current system locale and adjusting the locale to support correct numeral systems
 * based on the language.
 *
 * This interface defines methods for:
 * 1. Retrieving the current system locale.
 * 2. Retrieving the default system locale with adjustments for specific numeral systems
 *    (e.g., Devanagari numerals for Hindi, Marathi, Nepali, Arabic numerals for Arabic and
 *    related languages).
 */
interface LocaleProvider {

    /**
     * Retrieves the current system locale (the default locale).
     *
     * This method fetches the locale settings as defined by the system or environment,
     * without any adjustments for numeral systems. It simply returns the system's default
     * locale.
     *
     * @return The system's default locale as a [ULocale] object.
     */
    fun getCurrentLocale(): ULocale

    /**
     * Retrieves the default system locale and adjusts it to ensure the correct numeral system
     * is applied based on the language. This method will modify the locale to use appropriate
     * numeral systems, such as Devanagari numerals for Hindi, Marathi, Nepali, Arabic numerals
     * for Arabic and related languages, or standard numerals for other languages.
     *
     * @return The corrected locale with the appropriate numeral system for the default system locale.
     */
    fun getLocaleWithCorrectNumeralsForDefaultLocale(): ULocale

}

