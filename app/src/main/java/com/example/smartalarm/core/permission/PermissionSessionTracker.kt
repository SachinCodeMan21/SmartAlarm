package com.example.smartalarm.core.permission

import com.example.smartalarm.core.permission.model.AppFeature

/**
 * Keeps track of which features have already seen the
 * "Settings" dialog in this app session.
 */
object PermissionSessionTracker {
    // Stores a set of Pairs (Feature, Permission Type)
    private val shownSettingsDialogs = mutableSetOf<Pair<AppFeature, String>>()

    fun markAsShown(feature: AppFeature, permission: String) {
        shownSettingsDialogs.add(feature to permission)
    }

    fun hasBeenShown(feature: AppFeature, permission: String): Boolean {
        return shownSettingsDialogs.contains(feature to permission)
    }
}