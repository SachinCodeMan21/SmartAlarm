package com.example.smartalarm.core.permission.model

sealed class AppPermissionResult {
    abstract val permission: AppPermission
    abstract val status: PermissionStatus

    // Result for runtime permissions
    data class RuntimeResult(
        override val permission: AppPermission.Runtime,
        override val status: PermissionStatus.RuntimePermissionStatus
    ) : AppPermissionResult()

    // Result for special permissions
    data class SpecialResult(
        override val permission: AppPermission.Special,
        override val status: PermissionStatus.SpecialPermissionStatus
    ) : AppPermissionResult()
}
