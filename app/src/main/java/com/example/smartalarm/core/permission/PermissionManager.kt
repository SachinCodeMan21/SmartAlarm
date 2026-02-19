package com.example.smartalarm.core.permission

/**
 * Interface for managing and checking various permissions required by the application.
 *
 * This interface provides methods to check if specific permissions are granted for features like:
 * - Posting notifications
 * - Scheduling exact alarms
 * - Using full-screen intents
 * - Accessing sensor data for activity recognition
 */
interface PermissionManager {

    /**
     * Checks if the app has permission to post notifications.
     *
     * @return true if permission is granted, false otherwise.
     */
    fun isPostNotificationPermissionGranted(): Boolean

    /**
     * Checks if the app has permission to schedule exact alarms.
     *
     * @return true if permission is granted, false otherwise.
     */
    fun isScheduleExactAlarmPermissionGranted(): Boolean

    /**
     * Checks if the app has permission to use full-screen intents for notifications.
     *
     * @return true if permission is granted, false otherwise.
     */
    fun isFullScreenNotificationPermissionGranted(): Boolean

    /**
     * Checks if the app has permission to access sensor data for activity recognition.
     *
     * @return true if permission is granted, false otherwise.
     */
    fun isSensorPermissionGranted(): Boolean

}