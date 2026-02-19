package com.example.smartalarm.core.permission

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smartalarm.core.permission.model.AppPermission
import com.example.smartalarm.core.permission.model.PermissionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NewPermissionManagerImpl @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager
) : NewPermissionManager {

    override fun isGranted(permission: AppPermission): Boolean {
        return when (permission) {
            is AppPermission.Runtime -> {
                ContextCompat.checkSelfPermission(context, permission.permissionName) == PackageManager.PERMISSION_GRANTED
            }
            is AppPermission.Special -> checkSpecialPermission(permission)
        }
    }

    override fun getRuntimePermissionStatus(activity: Activity, permission: AppPermission.Runtime): PermissionStatus.RuntimePermissionStatus {
        val isGranted = ContextCompat.checkSelfPermission(context, permission.permissionName) == PackageManager.PERMISSION_GRANTED
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.permissionName)

        return when {
            isGranted -> PermissionStatus.RuntimePermissionStatus.Granted

            // User denied once; system flag is now true
            shouldShowRationale -> PermissionStatus.RuntimePermissionStatus.ShouldShowRationale

            // Flag is false: differentiate between "Fresh" and "Blocked"
            else -> {
                PermissionStatus.RuntimePermissionStatus.Denied
            }
        }
    }

    override fun getSpecialPermissionStatus(permission: AppPermission.Special): PermissionStatus.SpecialPermissionStatus {
        val isGranted = when (permission) {
            AppPermission.Special.SetExactAlarm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else true
            }
            AppPermission.Special.FullScreenNotification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    notificationManager.canUseFullScreenIntent()
                } else true
            }
        }

        return if (isGranted) {
            PermissionStatus.SpecialPermissionStatus.Granted
        } else {
            PermissionStatus.SpecialPermissionStatus.Denied
        }
    }
    private fun checkSpecialPermission(permission: AppPermission): Boolean {
        return when (permission) {
            AppPermission.Special.SetExactAlarm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else true
            }
            AppPermission.Special.FullScreenNotification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    notificationManager.canUseFullScreenIntent()
                } else true
            }
            else -> false
        }
    }
}