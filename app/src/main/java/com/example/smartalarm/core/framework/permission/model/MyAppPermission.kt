package com.example.smartalarm.core.framework.permission.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings


sealed interface MyAppPermission {

    val permissionKey: String


    // 1. Runtime Group (Standard System Pop-ups)
    sealed interface Runtime : MyAppPermission {

        val manifestName: String
        val hasCapability: Boolean

        override val permissionKey: String
            get() = manifestName

        data object PostNotifications : Runtime {
            // String literal avoids the API 33 compiler error
            override val manifestName = "android.permission.POST_NOTIFICATIONS"
            override val hasCapability: Boolean
                get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        }

        data object ActivityRecognition : Runtime {
            override val manifestName = "android.permission.ACTIVITY_RECOGNITION"
            override val hasCapability: Boolean
                get() = false
        }
    }

    // 2. Special Group (Redirects to System Settings)
    sealed interface Special : MyAppPermission {


        val name: String

        override val permissionKey: String
            get() = name


        /**
         * Returns the Intent to the specific Settings page.
         * We pass Context to get the Package Name.
         */
        fun getIntent(context: Context): Intent?

        data object ScheduleExactAlarms : Special {

            override val name = "schedule_exact_alarms"


            override fun getIntent(context: Context): Intent? {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                } else null
            }
        }
        data object FullScreenIntent : Special {

            override val name = "full_screen_intent"

            override fun getIntent(context: Context): Intent? {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                } else null
            }
        }

    }
}