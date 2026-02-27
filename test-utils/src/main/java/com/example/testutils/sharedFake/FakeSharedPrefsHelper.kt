package com.example.testutils.sharedFake

import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.core.framework.sharedPreference.impl.SharedPrefsHelperImpl
import javax.inject.Inject

/**
 * A mock implementation of [SharedPrefsHelper] used for testing.
 *
 * This class simulates shared preferences functionality by storing key-value pairs in a mutable map.
 * It provides getter and setter methods for the preferences related to the last opened home destination
 * and the last active alarm notification.
 *
 * The following preferences are supported:
 * - `lastOpenedHomeDestinationIdPrefs`: Stores the ID of the last opened home destination.
 * - `lastActiveAlarmNotificationPref`: Stores the ID of the last scheduled alarm notification.
 *
 * Key constants used for shared preferences:
 * - **PREF_HOME_LAST_OPENED_DESTINATION**: Key for storing the last opened home destination ID.
 * - **LAST_SCHEDULED_ALARM_NOTIFICATION**: Key for storing the last scheduled alarm notification ID.
 *
 * This class is designed for use in tests or as a fake shared preferences helper for development purposes.
 */
class FakeSharedPrefsHelper @Inject constructor() : SharedPrefsHelper {

    // Simulated shared preferences map
    private val prefs = mutableMapOf<String, Any>()

    /**
     * Gets the ID of the last opened home destination from shared preferences.
     *
     * @return The last opened home destination ID, or -1 if not set.
     */
    override var lastOpenedHomeDestinationIdPrefs: Int
        get() = prefs[SharedPrefsHelperImpl.PREF_HOME_LAST_OPENED_DESTINATION] as? Int ?: -1
        set(value) { prefs[SharedPrefsHelperImpl.PREF_HOME_LAST_OPENED_DESTINATION] = value }

    /**
     * Gets the ID of the last active alarm notification from shared preferences.
     *
     * @return The last scheduled alarm notification ID, or 0 if not set.
     */
    override var lastActiveAlarmNotificationPref: Int
        get() = prefs[SharedPrefsHelperImpl.LAST_SCHEDULED_ALARM_NOTIFICATION] as? Int ?: 0
        set(value) { prefs[SharedPrefsHelperImpl.LAST_SCHEDULED_ALARM_NOTIFICATION] = value }

    override var hasStopwatchRequestedNotificationPermission: Boolean
        get() = prefs[SharedPrefsHelperImpl.STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED] as? Boolean ?: false
        set(value) {prefs[SharedPrefsHelperImpl.STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED] = value}
    override var alarmSnoozeDurationMinutesPref: Int
        get() = 2
        set(value) {}
    override var alarmTimeoutDurationMinutesPref: Int
        get() = 3
        set(value) {}
}

