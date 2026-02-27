package com.example.smartalarm.core.framework.permission.model

data class Requirement(

    val permission: MyAppPermission,

    val rationaleTitle: String,

    val rationaleMessage: String,

    val toastOnDeny: String,

    val permanentlyDeniedTitle: String? = null,

    val permanentlyDeniedMessage: String? = null,

    val feature: AppFeature? = null
)