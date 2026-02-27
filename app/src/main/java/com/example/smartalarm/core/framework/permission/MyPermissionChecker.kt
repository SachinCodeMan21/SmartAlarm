package com.example.smartalarm.core.framework.permission

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.model.MyPermissionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MyPermissionChecker @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager
)
{

    /**
     * Now the Checker just gives you the facts.
     */
    fun checkRuntimeStatus(activity: Activity, permission: MyAppPermission.Runtime): MyPermissionStatus.RuntimeStatus {
        return when {
            isGranted(permission) -> MyPermissionStatus.RuntimeStatus.Granted
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.manifestName) -> {
                MyPermissionStatus.RuntimeStatus.ShowRationale
            }
            else -> MyPermissionStatus.RuntimeStatus.Denied
        }
    }

    fun checkSpecialStatus(permission: MyAppPermission.Special): MyPermissionStatus.SpecialStatus {
        return if (isGranted(permission)) MyPermissionStatus.SpecialStatus.Granted else MyPermissionStatus.SpecialStatus.Denied
    }

    /**
     * The main entry point.
     * Works for both Runtime and Special permissions.
     */
    fun isGranted(permission: MyAppPermission): Boolean {
        return when (permission) {
            is MyAppPermission.Runtime -> {
                checkRuntimePermission(permission)
            }
            is MyAppPermission.Special -> {
                checkSpecialPermission(permission)
            }
        }
    }

    private fun checkRuntimePermission(permission: MyAppPermission.Runtime): Boolean {
        return  if (permission.hasCapability){
            checkHasCapabilityGranted(permission,context)
        }
        else{
            ContextCompat.checkSelfPermission(context, permission.manifestName) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Handles the unique logic for permissions that live in Settings pages.
     */
    private fun checkSpecialPermission(permission: MyAppPermission.Special): Boolean {
        return when (permission) {

            MyAppPermission.Special.ScheduleExactAlarms -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else {
                    true // Below Android 12, this isn't restricted
                }
            }

            MyAppPermission.Special.FullScreenIntent -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    notificationManager.canUseFullScreenIntent()
                } else {
                    true // Below Android 14, this is granted by default
                }
            }
        }
    }

    private fun checkHasCapabilityGranted(permission: MyAppPermission.Runtime, context: Context): Boolean {
        return when (permission) {
            is MyAppPermission.Runtime.PostNotifications -> {
                // Only API 33+ has runtime + capability check
                if (permission.hasCapability){
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                }
                else{
                    false
                }
            }
            else -> false
        }
    }

}