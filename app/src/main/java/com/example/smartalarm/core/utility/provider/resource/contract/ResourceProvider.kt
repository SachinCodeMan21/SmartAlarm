package com.example.smartalarm.core.utility.provider.resource.contract

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

/**
 * Provides access to application resources in a platform-independent way.
 *
 * Responsibilities:
 * - Retrieve localized string resources.
 * - Retrieve formatted strings with placeholders.
 * - Retrieve string arrays from resources.
 *
 * This abstraction is useful for decoupling resource access from Android framework classes,
 * making code easier to test and more modular.
 */
interface ResourceProvider {

    /**
     * Returns a string associated with the given string resource ID.
     *
     * @param resId The resource ID of the string.
     * @return The localized string.
     */
    fun getString(@StringRes resId: Int): String

    /**
     * Returns a formatted string associated with the given string resource ID,
     * replacing placeholders with the provided arguments.
     *
     * @param resId The resource ID of the string.
     * @param args Arguments to format the string.
     * @return The formatted localized string.
     */
    fun getString(@StringRes resId: Int, vararg args: Any): String

    /**
     * Returns a string array associated with the given array resource ID.
     *
     * @param resId The resource ID of the string array.
     * @return The localized string array.
     */
    fun getStringArray(@ArrayRes resId: Int): Array<String>
}
