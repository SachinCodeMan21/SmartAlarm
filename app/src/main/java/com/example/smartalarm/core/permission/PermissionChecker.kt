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

class PermissionChecker @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager
) {

    fun isGranted(permission: AppPermission): Boolean {
        return when (permission) {
            is AppPermission.Runtime -> checkIsRuntimePermissionGranted(permission)
            is AppPermission.Special -> checkIsSpecialPermissionGranted(permission)
        }
    }

    fun checkPermissionStatus(permission: AppPermission, activity: Activity): PermissionStatus {
        return when (permission) {
            is AppPermission.Runtime -> getRuntimePermissionStatus(permission, activity)
            is AppPermission.Special -> getSpecialPermissionStatus(permission)
        }
    }



    // Status Mappers
    private fun getRuntimePermissionStatus(runtimePermission: AppPermission.Runtime, activity: Activity): PermissionStatus.RuntimePermissionStatus {

        val granted = isGranted(runtimePermission)
        val shouldShowRationale = activity.let { ActivityCompat.shouldShowRequestPermissionRationale(it, runtimePermission.permissionName) }
        return when{
            granted -> PermissionStatus.RuntimePermissionStatus.Granted
            shouldShowRationale -> PermissionStatus.RuntimePermissionStatus.ShouldShowRationale
            else -> PermissionStatus.RuntimePermissionStatus.Denied
        }
    }
    private fun getSpecialPermissionStatus(specialPermission: AppPermission.Special): PermissionStatus.SpecialPermissionStatus {
        val isGranted = checkIsSpecialPermissionGranted(specialPermission)
        return if (isGranted) {
            PermissionStatus.SpecialPermissionStatus.Granted
        } else {
            PermissionStatus.SpecialPermissionStatus.Denied
        }
    }


    // Helper Function
    private fun checkIsRuntimePermissionGranted(permission: AppPermission.Runtime): Boolean {
        return ContextCompat.checkSelfPermission(context, permission.permissionName) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkIsSpecialPermissionGranted(permission: AppPermission.Special): Boolean {
        return when (permission) {
            AppPermission.Special.ScheduleExactAlarm -> {
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
    }
}
