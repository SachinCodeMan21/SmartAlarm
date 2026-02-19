package com.example.smartalarm.core.permission

import android.app.Activity
import com.example.smartalarm.core.permission.model.AppPermission
import com.example.smartalarm.core.permission.model.PermissionStatus

interface NewPermissionManager {
    fun isGranted(permission: AppPermission): Boolean
    fun getRuntimePermissionStatus(activity: Activity, permission: AppPermission.Runtime) : PermissionStatus.RuntimePermissionStatus
    fun getSpecialPermissionStatus(permission: AppPermission.Special) : PermissionStatus.SpecialPermissionStatus
}