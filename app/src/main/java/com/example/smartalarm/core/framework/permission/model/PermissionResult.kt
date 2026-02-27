package com.example.smartalarm.core.framework.permission.model

sealed class PermissionResult {

    sealed class RuntimePermissionResult : PermissionResult() {
        object Granted : RuntimePermissionResult()
        object Denied : RuntimePermissionResult()
        object PermanentlyDenied : RuntimePermissionResult()
    }

    sealed class SpecialPermissionResult : PermissionResult() {
        object Granted : SpecialPermissionResult()
        object Denied : SpecialPermissionResult()
    }
}

