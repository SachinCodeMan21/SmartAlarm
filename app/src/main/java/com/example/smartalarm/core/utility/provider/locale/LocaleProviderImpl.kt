package com.example.smartalarm.core.utility.provider.locale

import android.icu.util.ULocale
import javax.inject.Inject

/**
 * Implementation of the [LocaleProvider] interface that provides methods for retrieving
 * the current locale and adjusting it to account for locale-specific numeral systems.
 *
 * This class handles the retrieval of the system's default locale and adjusts it to
 * support different numeral systems based on the language. It also provides a method to
 * get the locale with corrected numerals for languages that use different numeral systems
 * (e.g., Devanagari numerals for Hindi, Marathi, Nepali, Arabic numerals for Arabic-speaking
 * languages, etc.).
 */
class LocaleProviderImpl @Inject constructor() : LocaleProvider {

    /**
     * Retrieves the current system locale (the default locale).
     *
     * This method fetches the locale settings as defined by the system or environment.
     * It does not modify the locale or adjust for numerals.
     *
     * @return The system's default locale as a [ULocale] object.
     */
    override fun getCurrentLocale(): ULocale {
        return ULocale.getDefault()
    }

    /**
     * Retrieves the default system locale and adjusts it for the correct numeral system
     * based on the language. This method accounts for languages that use different numeral
     * systems (e.g., Devanagari numerals for Hindi, Marathi, Nepali, Arabic numerals for
     * Arabic-speaking languages, etc.).
     *
     * @return The corrected locale with adjusted numerals (if applicable), as a [ULocale] object.
     */
    override fun getLocaleWithCorrectNumeralsForDefaultLocale(): ULocale {
        val locale = ULocale.getDefault()  // Get the system's default locale
        return getLocaleWithCorrectNumerals(locale)  // Adjust locale for numerals
    }

    /**
     * Adjusts the given locale to ensure that the correct numeral system is applied based
     * on the language. This method modifies the locale to use Devanagari numerals for Hindi,
     * Marathi, and Nepali, Arabic numerals for Arabic and related languages, and returns the
     * original locale for others.
     *
     * @param locale The input [ULocale] to adjust based on the numeral system.
     * @return The adjusted [ULocale] with the appropriate numeral system (if applicable).
     */
    private fun getLocaleWithCorrectNumerals(locale: ULocale): ULocale {
        return when (locale.language) {
            "hi", "mr", "ne" -> ULocale(locale.toLanguageTag() + "@numbers=deva") // Devanagari numerals for Hindi/Marathi/Nepali
            "ar", "fa", "ps", "ur" -> ULocale(locale.toLanguageTag() + "@numbers=arab") // Arabic numerals for Arabic/Farsi/Pashto/Urdu
            "zh", "ja", "ko" -> locale // Use standard Arabic numerals for Chinese/Japanese/Korean
            else -> locale // Default case (Arabic numerals for most other languages)
        }
    }
}
