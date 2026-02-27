package com.example.smartalarm.core.framework.sharedPreference.impl

import android.content.SharedPreferences
import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SharedPrefsHelper] using [SharedPreferences] for persistent key-value storage.
 *
 * Provides generic get/set utilities for common primitive types.
 */
@Singleton
class SharedPrefsHelperImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPrefsHelper {

    companion object {
        const val PREF_HOME_LAST_OPENED_DESTINATION = "LAST_OPENED_FRAGMENT"
        const val LAST_SCHEDULED_ALARM_NOTIFICATION = "LAST_SCHEDULED_ALARM_NOTIFICATION"

        const val STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED = "STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED"


        // New Keys for Alarms
        const val PREF_ALARM_SNOOZE_DURATION_MINUTES = "ALARM_SNOOZE_DURATION_MINUTES"
        const val PREF_ALARM_TIMEOUT_DURATION_MINUTES = "ALARM_TIMEOUT_DURATION_MINUTES"

        // Defaults
        private const val DEFAULT_SNOOZE_MINUTES = 10
        private const val DEFAULT_TIMEOUT_MINUTES = 15


    }

    /**
     * Gets or sets the ID of the last opened home destination.
     *
     * Used to persist and retrieve the last selected home screen destination across app launches.
     * Defaults to -1 if no value has been saved.
     */
    override var lastOpenedHomeDestinationIdPrefs: Int
        get() = getPref(PREF_HOME_LAST_OPENED_DESTINATION, -1)
        set(value) = setPref(PREF_HOME_LAST_OPENED_DESTINATION, value)

    /**
     * Gets or sets the ID of the last active alarm notification.
     *
     * Used to persist and retrieve the last scheduled alarm notification ID.
     * Defaults to 0 if no value has been saved.
     */
    override var lastActiveAlarmNotificationPref: Int
        get() = getPref(LAST_SCHEDULED_ALARM_NOTIFICATION, 0)
        set(value) = setPref(LAST_SCHEDULED_ALARM_NOTIFICATION, value)

    /**
     * Indicates whether the stopwatch has already requested the POST_NOTIFICATIONS permission.
     *
     * Used to ensure the permission prompt is shown only once. If false, the app may request
     * the permission; if true, the stopwatch runs without re-prompting, even if permission
     * was denied.
     */
    override var hasStopwatchRequestedNotificationPermission: Boolean
        get() = getPref(STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED, false)
        set(value) = setPref(STOPWATCH_POST_NOTIFICATION_PERMISSION_REQUESTED, value)


    /**
     * Gets or sets the user's preferred snooze duration in minutes.
     * Defaults to 5 minutes.
     */
    override var alarmSnoozeDurationMinutesPref: Int
        get() = getPref(PREF_ALARM_SNOOZE_DURATION_MINUTES, DEFAULT_SNOOZE_MINUTES)
        set(value) = setPref(PREF_ALARM_SNOOZE_DURATION_MINUTES, value)

    /**
     * Gets or sets the user's preferred alarm timeout (auto-silence) in minutes.
     * Defaults to 10 minutes.
     */
    override var alarmTimeoutDurationMinutesPref: Int
        get() = getPref(PREF_ALARM_TIMEOUT_DURATION_MINUTES, DEFAULT_TIMEOUT_MINUTES)
        set(value) = setPref(PREF_ALARM_TIMEOUT_DURATION_MINUTES, value)


    /**
     * Reads a value of type [T] from SharedPreferences.
     *
     * @param key The preference key.
     * @param default Default value if key is not found.
     * @return Stored value or default.
     * @throws IllegalArgumentException for unsupported types.
     */
    private inline fun <reified T> getPref(key: String, default: T): T {
        return when (T::class) {
            Int::class    -> sharedPreferences.getInt(key, default as Int) as T
            Long::class   -> sharedPreferences.getLong(key, default as Long) as T
            Boolean::class-> sharedPreferences.getBoolean(key, default as Boolean) as T
            Float::class  -> sharedPreferences.getFloat(key, default as Float) as T
            String::class -> sharedPreferences.getString(key, default as String) as T
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
    }

    /**
     * Stores a value of type [T] in SharedPreferences.
     *
     * @param key The preference key.
     * @param value Value to store.
     * @throws IllegalArgumentException for unsupported types.
     */
    private inline fun <reified T> setPref(key: String, value: T) {
        sharedPreferences.edit().apply {
            when (T::class) {
                Int::class    -> putInt(key, value as Int)
                Long::class   -> putLong(key, value as Long)
                Boolean::class-> putBoolean(key, value as Boolean)
                Float::class  -> putFloat(key, value as Float)
                String::class -> putString(key, value as String)
                else -> throw IllegalArgumentException("Unsupported preference type")
            }
            apply()
        }
    }

}
