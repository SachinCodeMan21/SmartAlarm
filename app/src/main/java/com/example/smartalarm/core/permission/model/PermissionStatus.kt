package com.example.smartalarm.core.permission.model

sealed class PermissionStatus {

    // Runtime permissions statuses
    sealed class RuntimePermissionStatus : PermissionStatus() {
        object Granted : RuntimePermissionStatus()
        object ShouldShowRationale : RuntimePermissionStatus()
        object Denied : RuntimePermissionStatus()
    }

    // Special permissions statuses
    sealed class SpecialPermissionStatus : PermissionStatus() {
        object Granted : SpecialPermissionStatus()
        object Denied : SpecialPermissionStatus()
    }
}
