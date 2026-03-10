package com.example.smartalarm.feature.setting.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a language option in the app.
 *
 * This data class holds all the necessary information to display a language
 * in a list, including its display name, language code, and associated icon.
 *
 * @property nameResId Resource ID of the language's display name (String resource).
 * @property code ISO or custom code representing the language (e.g., "en", "fr").
 * @property iconResId Resource ID of the language icon or flag (Drawable resource).
 */
data class LanguageItem(
    @field:StringRes val nameResId: Int,    // Indicates this is a string resource ID
    val code: String,
    @field:DrawableRes val iconResId: Int   // Indicates this is a drawable resource ID
)
