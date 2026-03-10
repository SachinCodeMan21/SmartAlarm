package com.example.smartalarm.feature.setting.utility

import android.content.Context
import com.example.smartalarm.R
import com.example.smartalarm.feature.setting.model.LanguageItem

/**
 * Provides a list of supported languages in the application.
 *
 * This object is responsible for generating and returning a list of [LanguageItem]s,
 * each representing a language with its display name, locale code, and icon.
 */
object LanguageProvider {

    /**
     * Returns a list of all supported languages, sorted alphabetically by their
     * localized display names.
     *
     * @return A [List] of [LanguageItem], each containing the string resource ID,
     *         locale code, and drawable resource ID for the language icon.
     *
     * @see LanguageItem
     */
    fun getLanguageList(): List<LanguageItem> {

        val languages = listOf(
            LanguageItem(R.string.chinese, "zh", R.drawable.icon_chinese),
            LanguageItem(R.string.english, "en", R.drawable.icon_english),
            LanguageItem(R.string.french, "fr", R.drawable.icon_french),
            LanguageItem(R.string.german, "de", R.drawable.icon_german),
            LanguageItem(R.string.hindi, "hi", R.drawable.icon_hindi),
            LanguageItem(R.string.japanese, "ja", R.drawable.icon_japanese),
            LanguageItem(R.string.korean, "ko", R.drawable.icon_korean),
            LanguageItem(R.string.portuguese, "pt", R.drawable.icon_portuguese),
            LanguageItem(R.string.spanish, "es", R.drawable.icon_spanish)
        )

        return languages
    }
}