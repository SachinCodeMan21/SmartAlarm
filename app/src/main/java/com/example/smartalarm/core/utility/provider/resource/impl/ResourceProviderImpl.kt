package com.example.smartalarm.core.utility.provider.resource.impl

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Default implementation of [ResourceProvider] using Android's [Context] to access resources.
 *
 * Responsibilities:
 * - Provides localized string resources via [Context.getString].
 * - Supports formatted strings with placeholders.
 * - Provides string arrays from resources.
 *
 * This class is designed for dependency injection and can be used wherever [ResourceProvider] is required.
 *
 * @property context The application context used to access resources. Injected via Hilt's [ApplicationContext].
 */
class ResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    /**
     * Returns a string associated with the given string resource ID.
     */
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    /**
     * Returns a formatted string associated with the given string resource ID,
     * replacing placeholders with the provided arguments.
     */
    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    /**
     * Returns a string array associated with the given array resource ID.
     */
    override fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }
}
