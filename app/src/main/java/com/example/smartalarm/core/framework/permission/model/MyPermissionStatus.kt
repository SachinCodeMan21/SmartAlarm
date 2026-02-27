package com.example.smartalarm.core.framework.permission.model

sealed interface MyPermissionStatus {

    // Standard Runtime Statuses
    sealed interface RuntimeStatus : MyPermissionStatus {
        data object Granted : RuntimeStatus
        data object ShowRationale : RuntimeStatus // Show custom explanation first
        data object Denied : RuntimeStatus // Launch Permission
    }

    // Special Statuses (Simpler flow)
    sealed interface SpecialStatus : MyPermissionStatus {
        data object Granted : SpecialStatus
        data object Denied : SpecialStatus // Just needs a "Go to Settings" dialog
    }
}