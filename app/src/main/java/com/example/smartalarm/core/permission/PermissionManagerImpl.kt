package com.example.smartalarm.core.permission

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implementation of the [PermissionManager] interface to check permissions at runtime.
 *
 * This class implements permission checking for specific features such as posting notifications,
 * scheduling exact alarms, using full-screen intents, and accessing sensor data. It uses
 * Android's permission APIs to check if the required permissions are granted.
 *
 * @param context The application context used for checking permissions.
 * @param alarmManager The [android.app.AlarmManager] used to check alarm-related permissions.
 * @param notificationManager The [android.app.NotificationManager] used to check notification-related permissions.
 */
class PermissionManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager
) : PermissionManager {


    /**
     * Checks if the app has permission to post notifications.
     *
     * This method checks for the `POST_NOTIFICATIONS` permission, which is required starting from Android 13.
     * For Android versions below 13, this permission is implicitly granted.
     *
     * @return true if permission is granted, false otherwise.
     */
    override fun isPostNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true  // No need for this permission on versions below Android 13
        }
    }

    /**
     * Checks if the app has permission to schedule exact alarms.
     *
     * This method checks for the `SCHEDULE_EXACT_ALARM` permission, which is required starting from Android 12 (API level 31).
     * For Android versions below 12, this permission is implicitly granted.
     *
     * @return true if permission is granted, false otherwise.
     */
    override fun isScheduleExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true  // No need for this permission on versions below Android 12
        }
    }

    /**
     * Checks if the app has permission to use full-screen intents for notifications.
     *
     * This method checks for full-screen intent support based on the Android version:
     * - For Android 14 (API level 34) and higher, it checks if full-screen intents are allowed.
     * - For Android 13 (API level 33), it checks if the `POST_NOTIFICATIONS` permission is granted.
     * - For versions below Android 13, full-screen intents are implicitly allowed.
     *
     * @return true if permission is granted, false otherwise.
     */
    override fun isFullScreenNotificationPermissionGranted(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14 (API 34) or higher
                notificationManager.canUseFullScreenIntent() // Check if full-screen intent is supported by the device
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13 (API 33)
                // Check if POST_NOTIFICATIONS permission is granted (required for posting notifications)
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                true // No special permission needed below Android 13 for full-screen notifications
            }
        }
    }

    /**
     * Checks if the app has permission to access sensor data for activity recognition.
     *
     * This method checks for the `ACTIVITY_RECOGNITION` permission, which is required starting from Android 10 (API level 29).
     * For versions below Android 10, this permission is implicitly granted.
     *
     * @return true if permission is granted, false otherwise.
     */
    override fun isSensorPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check if the permission is granted for devices running Android 10 (API level 29) or higher
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission is implicitly granted for devices below Android 10
            true
        }
    }
}