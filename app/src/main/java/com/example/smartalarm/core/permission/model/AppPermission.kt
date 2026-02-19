package com.example.smartalarm.core.permission.model

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.smartalarm.R


sealed class AppPermission(
    val permissionName: String,
    val friendlyNameResId: Int,
    val rationaleTitleResId: Int?,
    val rationaleMessageResId: Int?,
    val permanentlyDeniedRationaleResId: Int? = null // Rationale for when permission is permanently denied
) {

    /**
     * Runtime permissions: "Dangerous" permissions that require the user to explicitly
     * grant access via a system dialog while the app is running.
     */
    sealed class Runtime(
        name: String,
        friendlyNameResId: Int,
        rationaleTitleResId: Int,
        rationaleMessageResId: Int,
        permanentlyDeniedRationaleResId: Int? = null
    ) : AppPermission(name, rationaleTitleResId, rationaleMessageResId, permanentlyDeniedRationaleResId) {

        // Android 13+ Notification permission
        data object PostNotifications : Runtime(
            Manifest.permission.POST_NOTIFICATIONS,
            friendlyNameResId = R.string.perm_friendly_name_notifications,
            R.string.notification_permission_required_title,
            R.string.post_notification_permission_rationale_message,
            R.string.notifications_permission_permanent_denial
        )

        // Android 13+ Activity recognition
        data object ActivityRecognition : Runtime(
            Manifest.permission.ACTIVITY_RECOGNITION,
            friendlyNameResId = R.string.perm_friendly_name_activity,
            R.string.activity_recognition_permission_required_title,
            R.string.activity_recognition_permission_rationale_message,
            R.string.activity_recognition_permission_permanent_denial
        )
    }

    /**
     * Special permissions: These are sensitive permissions that cannot be requested
     * via the standard runtime dialog. The user must be sent to a specific screen
     * in System Settings to toggle them manually.
     */
    sealed class Special(
        name: String,
        friendlyNameResId: Int,
        rationaleTitleResId: Int,
        rationaleMessageResId: Int,
        permanentlyDeniedRationaleResId: Int? = null
    ) : AppPermission(name, rationaleTitleResId, rationaleMessageResId, permanentlyDeniedRationaleResId) {

        abstract fun getIntent(context: Context): Intent

        // Permission for full-screen notifications (overlay permission already included in SYSTEM_ALERT_WINDOW)
        data object FullScreenNotification : Special(
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            friendlyNameResId = R.string.perm_friendly_name_fullscreen,
            R.string.fullscreen_notification_rationale_title,
            R.string.fullscreen_notification_rationale_message,
            R.string.fullscreen_notification_permission_permanent_denial
        ){
            override fun getIntent(context: Context): Intent = Intent(
                Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                Uri.parse("package:${context.packageName}")
            )
        }

        // Permission for setting exact alarms
        data object ScheduleExactAlarm : Special(
            Manifest.permission.SET_ALARM,
            friendlyNameResId = R.string.perm_friendly_name_alarms,
            R.string.set_alarm_rationale_title,
            R.string.set_alarm_rationale_message,
            null // Permanent denial typically not needed for SET_ALARM
        ){
            override fun getIntent(context: Context): Intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            )
        }
    }
}