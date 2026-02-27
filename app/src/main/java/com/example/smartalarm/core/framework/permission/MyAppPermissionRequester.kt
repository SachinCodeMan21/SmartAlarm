package com.example.smartalarm.core.framework.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.model.PermissionResult
import com.example.smartalarm.core.framework.permission.model.RequesterType

class MyAppPermissionRequester(
    private val caller: ActivityResultCaller,
    private val lifecycle: Lifecycle,
    private val checker: MyPermissionChecker,
    private val type: RequesterType = RequesterType.BOTH
) : DefaultLifecycleObserver
{

    // Separate launchers to prevent collisions
    private var runtimeLauncher: ActivityResultLauncher<String>? = null
    private var specialLauncher: ActivityResultLauncher<Intent>? = null
    private var settingsLauncher: ActivityResultLauncher<Intent>? = null



    // Separate callbacks to prevent collisions
    private var runtimeCallback: ((PermissionResult.RuntimePermissionResult) -> Unit)? = null
    private var specialCallback: ((PermissionResult.SpecialPermissionResult) -> Unit)? = null

    private var settingsCallback: ((Boolean) -> Unit)? = null



    private var pendingRuntimePermission: MyAppPermission.Runtime? = null
    private var pendingSpecialPermission: MyAppPermission.Special? = null




    // Lifecycle Methods
    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {

        // We always register the settingsLauncher because any requester might
        // need to redirect to settings if a permission is permanently denied.
        settingsLauncher = caller.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            handleSettingsResult()
        }
        when (type) {
            RequesterType.RUNTIME_ONLY -> registerRuntime()
            RequesterType.SPECIAL_ONLY -> registerSpecial()
            RequesterType.BOTH -> {
                registerRuntime()
                registerSpecial()
            }
        }
    }
    override fun onDestroy(owner: LifecycleOwner) {
        lifecycle.removeObserver(this)
        runtimeLauncher = null
        specialLauncher = null
        runtimeCallback = null
        specialCallback = null
        pendingRuntimePermission = null
        pendingSpecialPermission = null
    }


    // Register Permission Methods
    private fun registerRuntime() {
        runtimeLauncher = caller.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted -> handleRuntimeResult(isGranted) }
    }
    private fun registerSpecial() {
        specialLauncher = caller.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { handleSpecialResult() }
    }


    // Permission Requester
    /**
     * Entry point for standard Android permissions (Notifications, etc.)
     **/
    fun requestRuntimePermission(
        permission: MyAppPermission.Runtime,
        onResult: (PermissionResult.RuntimePermissionResult) -> Unit
    ) {
        val launcher = runtimeLauncher
            ?: throw IllegalStateException("Runtime launcher not registered for this requester type")

        this.runtimeCallback = onResult
        this.pendingRuntimePermission = permission
        launcher.launch(permission.manifestName)
    }

    /**
     * Redirects to App Info. On return, it re-checks the specific permission
     * and returns true if the user enabled it.
     */
    fun requestAppSettings(
        permission: MyAppPermission.Runtime,
        onResult: (Boolean) -> Unit
    ) {
        val launcher = settingsLauncher ?: return
        val context = getContext() ?: return

        this.settingsCallback = onResult
        this.pendingRuntimePermission = permission

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        launcher.launch(intent)
    }

    /**
     * Entry point for Settings-based permissions (Alarms, Full Screen)
     */
    fun requestSpecialPermission(
        permission: MyAppPermission.Special,
        onResult: (PermissionResult.SpecialPermissionResult) -> Unit
    ) {

        val launcher = specialLauncher
            ?: throw IllegalStateException("Special Permission launcher not registered for this requester type")

        this.specialCallback = onResult
        this.pendingSpecialPermission = permission

        val context = getContext() ?: return
        val intent = permission.getIntent(context)

        if (intent != null) {
            launcher.launch(intent)
        } else {
            onResult(PermissionResult.SpecialPermissionResult.Granted)
            specialCallback = null
            pendingSpecialPermission = null
        }
    }




    // Handle Permission Launcher Result
    private fun handleRuntimeResult(isGranted: Boolean) {

        val activity = getActivity()

        val shouldShowRationale = if (activity!=null && pendingRuntimePermission!=null){
            ActivityCompat.shouldShowRequestPermissionRationale(activity, pendingRuntimePermission!!.manifestName)
        } else{
            false
        }

        val result = when {
            isGranted -> PermissionResult.RuntimePermissionResult.Granted
            shouldShowRationale-> PermissionResult.RuntimePermissionResult.Denied
            else -> PermissionResult.RuntimePermissionResult.PermanentlyDenied
        }

        runtimeCallback?.invoke(result)
        cleanupRuntime()
    }
    private fun handleSpecialResult() {
        val permission = pendingSpecialPermission ?: return
        val result = if (checker.isGranted(permission)) {
            PermissionResult.SpecialPermissionResult.Granted
        } else {
            PermissionResult.SpecialPermissionResult.Denied
        }

        specialCallback?.invoke(result)
        cleanupSpecial()
    }
    private fun handleSettingsResult() {
        val permission = pendingRuntimePermission ?: return

        // The fact: Is it granted after they came back?
        val isNowGranted = checker.isGranted(permission)

        settingsCallback?.invoke(isNowGranted)

        // Clean up
        settingsCallback = null
        pendingRuntimePermission = null
    }

    private fun cleanupRuntime() {
        runtimeCallback = null
        pendingRuntimePermission = null
    }
    private fun cleanupSpecial() {
        specialCallback = null
        pendingSpecialPermission = null
    }



    // Helper Functions
    private fun getContext(): Context? {
        return when (caller) {
            is Context -> caller
            is Fragment -> caller.context
            else -> null
        }
    }
    private fun getActivity(): Activity? {
        return when (caller) {
            is Activity -> caller
            is Fragment -> caller.activity
            else -> null
        }
    }

}