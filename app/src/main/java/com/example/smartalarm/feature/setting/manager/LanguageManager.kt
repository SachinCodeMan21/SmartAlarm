package com.example.smartalarm.feature.setting.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat

object LanguageManager {

    private const val PREF_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "app_language"

    fun setLanguage(context: Context, lang: String) {
        // Save selection
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, lang) }

        // Convert string to LocaleList
        val localeList = when (lang) {
            "system" -> LocaleListCompat.getEmptyLocaleList()
            else -> LocaleListCompat.forLanguageTags(lang)
        }

        // Apply new locale
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "system") ?: "system"
    }

}