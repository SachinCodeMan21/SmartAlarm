package com.example.smartalarm.core.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.core.permission.model.AppPermission
import com.example.smartalarm.core.permission.model.PermissionLauncherType
import com.example.smartalarm.core.permission.model.PermissionResult

class PermissionRequester(
    private val caller: ActivityResultCaller,
    lifecycleOwner: LifecycleOwner,
    private val context : Context,
    private val permissionChecker: PermissionChecker,
    private val requiredLaunchers: List<PermissionLauncherType> = listOf(PermissionLauncherType.SINGLE_PERMISSION),
    private val rationaleProvider: (String) -> Boolean
) : DefaultLifecycleObserver {


    private var lastRequestedPermission: AppPermission? = null
    private var onPermissionResult: ((PermissionResult) -> Unit)? = null

    private var singlePermissionLauncher: ActivityResultLauncher<String>? = null
    private var settingsPermissionLauncher: ActivityResultLauncher<Intent>? = null

    init {
        // Automatically observe the activity's lifecycle
        lifecycleOwner.lifecycle.addObserver(this)
        registerPermissionLaunchers()
    }

    private fun registerPermissionLaunchers(){

        // We only register what the Activity actually needs
        if (requiredLaunchers.contains(PermissionLauncherType.SINGLE_PERMISSION)) {

            singlePermissionLauncher = caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

                // Pure type safety: The caller tells us if we should show rationale
                val shouldShowRationale = lastRequestedPermission?.let { rationaleProvider(it.permissionName) } ?: false

                val result = when {
                    isGranted -> PermissionResult.RuntimePermissionResult.Granted
                    shouldShowRationale -> PermissionResult.RuntimePermissionResult.Denied
                    else -> PermissionResult.RuntimePermissionResult.PermanentlyDenied
                }
                onPermissionResult?.invoke(result)
            }

        }

        if (requiredLaunchers.contains(PermissionLauncherType.SPECIAL_PERMISSION)) {

            settingsPermissionLauncher = caller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // Settings don't return a 'granted' boolean; the app must manually re-verify
                val isGranted = lastRequestedPermission?.let { permissionChecker.isGranted(it) } ?: false
                val result = when{
                    isGranted -> PermissionResult.SpecialPermissionResult.Granted
                    else -> PermissionResult.SpecialPermissionResult.Denied
                }
                onPermissionResult?.invoke(result)
            }

        }

    }


    /**
     * Handles standard runtime permissions (e.g., Camera, Mic).
     * Triggers Step 6: Request the permission to show the system dialog.
     */
    fun requestSinglePermission(permission: AppPermission, onResult: (PermissionResult) -> Unit) {
        prepareRequest(permission, onResult)
        when(permission){
            is AppPermission.Runtime -> {
                singlePermissionLauncher?.launch(permission.permissionName)
                    ?: error("Single launcher not registered")
            }
            is AppPermission.Special -> {
                try {
                    val intent = permission.getIntent(context)
                    settingsPermissionLauncher?.launch(intent)
                        ?: error("Settings launcher not registered")
                } catch (_: ActivityNotFoundException) {
                    // Fallback: If the specific settings page fails,
                    // take them to the general "App Info" page as a backup.
                    val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    settingsPermissionLauncher?.launch(fallbackIntent)
                }
            }
        }
    }

    private fun prepareRequest(permission: AppPermission, onResult: (PermissionResult) -> Unit) {
        this.onPermissionResult = onResult
        this.lastRequestedPermission = permission
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // Clean up references to prevent memory leaks
        onPermissionResult = null
        super.onDestroy(owner)
    }

}