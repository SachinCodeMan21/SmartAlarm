package com.example.smartalarm.core.framework.sharedPreference.contract

/**
 * Abstraction for accessing app-specific key-value data using SharedPreferences.
 *
 * Useful for persisting lightweight state such as navigation or notification metadata.
 */
interface SharedPrefsHelper {

    /**
     * Stores or retrieves the last opened destination ID on the home screen.
     * Defaults to -1 if not set.
     */
    var lastOpenedHomeDestinationIdPrefs: Int

    /**
     * Stores or retrieves the last active alarm notification ID.
     * Defaults to 0 if not set.
     */
    var lastActiveAlarmNotificationPref: Int

    /**
     * Indicates whether the stopwatch has already requested the POST_NOTIFICATIONS permission.
     *
     * Used to ensure the permission prompt is shown only once. If false, the app may request
     * the permission; if true, the stopwatch runs without re-prompting, even if permission
     * was denied.
     */
    var hasStopwatchRequestedNotificationPermission: Boolean

    /**
     * Gets or sets the user's preferred snooze duration in minutes.
     * Defaults to 5 minutes.
     */
    var alarmSnoozeDurationMinutesPref: Int

    /**
     * Gets or sets the user's preferred alarm timeout (auto-silence) in minutes.
     * Defaults to 10 minutes.
     */
    var alarmTimeoutDurationMinutesPref: Int


}
