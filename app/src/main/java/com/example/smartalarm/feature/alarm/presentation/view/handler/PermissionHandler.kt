package com.example.smartalarm.feature.alarm.presentation.view.handler

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.core.utility.extension.isSdk31AndAbove
import com.example.smartalarm.core.utility.extension.isSdk33AndAbove
import com.example.smartalarm.feature.home.presentation.dialog.ExactAlarmPermissionDialog
import com.example.smartalarm.feature.home.presentation.dialog.FullScreenNotificationPermissionDialog
import com.example.smartalarm.feature.home.presentation.dialog.PermissionRationaleDialog
import com.example.smartalarm.feature.home.presentation.dialog.PermissionSettingsDialog

/**
 * A handler class for managing permissions related to alarm editor functionality.
 *
 * This class is responsible for requesting permissions, handling results from permission
 * dialogs, and guiding the user to the appropriate settings screens for granting permissions
 * such as notifications, full-screen notifications, exact alarm scheduling, and app settings.
 * It uses ActivityResultContracts and FragmentResultListeners to interact with the user in a
 * lifecycle-aware way, ensuring that permissions are handled properly within the lifecycle of the fragment.
 *
 * The class provides the following key features:
 * - Requesting permissions such as POST_NOTIFICATIONS, Full-Screen Notifications, Schedule Exact Alarm, and App Settings.
 * - Handling the results of permission requests and showing appropriate dialogs or toasts.
 * - Directing users to app settings when necessary, or prompting them to grant permissions through system dialogs.
 * - A custom action can be executed when the POST_NOTIFICATIONS permission is granted, through the `onPostNotificationGranted` lambda.
 *
 * @param fragment The fragment from which permissions are requested. The fragment's lifecycle is observed to ensure no memory leaks.
 * @param myPermissionManager A custom permission manager used to check if certain permissions are granted.
 * @param onPostNotificationGranted A lambda function that defines the custom action to be executed when the POST_NOTIFICATIONS permission is granted.
 */
class PermissionHandler(
    private val fragment: Fragment,
    private val myPermissionManager: PermissionManager,
    private val onPostNotificationGranted: () -> Unit
) : DefaultLifecycleObserver
{


    // Standard Android context access from Fragment
    private val context: Context get() = fragment.requireContext()
    private val fragmentManager: FragmentManager get() = fragment.parentFragmentManager



    // Launchers must be initialized during Fragment/Activity initialization (init block is fine)
    private lateinit var postNotificationLauncher: ActivityResultLauncher<String>
    private lateinit var fullScreenNotificationLauncher: ActivityResultLauncher<Intent>
    private lateinit var scheduleExactAlarmLauncher: ActivityResultLauncher<Intent>
    private lateinit var appSettingLauncher: ActivityResultLauncher<Intent>


    // Init Block
    init {
        // Observe viewLifecycleOwner to avoid leaks after Fragment's view is destroyed
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
        registerPermissionLaunchers()
    }

    override fun onCreate(owner: LifecycleOwner) {
        // Listeners can be registered in onCreate to ensure they are ready before view is created
        registerPermissionFragmentResultListeners(owner)
    }



    //--------------------------------------------------------
    // Request Permission Launcher Methods
    //--------------------------------------------------------
    fun requestPostNotification() {
        if (context.isSdk33AndAbove) {
            postNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun requestFullScreenNotificationPermission() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        fullScreenNotificationLauncher.launch(intent)
    }

    fun requestExactAlarmPermission() {
        if (context.isSdk31AndAbove) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            scheduleExactAlarmLauncher.launch(intent)
        }
    }

    fun requestAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        appSettingLauncher.launch(intent)
    }


    //----------------------------------------------------------------------
    // Register Permission Launchers & Dialog Fragment Result Listeners
    //-----------------------------------------------------------------------
    private fun registerPermissionLaunchers() {

        postNotificationLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::handlePostNotificationResult
        )

        fullScreenNotificationLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { handlePermissionCallback(myPermissionManager.isFullScreenNotificationPermissionGranted()) }

        scheduleExactAlarmLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { handlePermissionCallback(myPermissionManager.isScheduleExactAlarmPermissionGranted()) }

        appSettingLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { handlePermissionCallback(myPermissionManager.isPostNotificationPermissionGranted()) }

    }
    private fun registerPermissionFragmentResultListeners(owner: LifecycleOwner) {
        // Mapping of Result Keys to Actions to reduce boilerplate
        val configurations = listOf(
            Triple(PermissionRationaleDialog.RATIONALE_DIALOG_KEY, PermissionRationaleDialog.RATIONALE_DIALOG_ACTION_KEY, PermissionRationaleDialog.ACTION_GRANT) to { requestPostNotification() },
            Triple(FullScreenNotificationPermissionDialog.FULL_SCREEN_PERMISSION_DIALOG_RESULT_KEY, FullScreenNotificationPermissionDialog.FULL_SCREEN_PERMISSION_DIALOG_BUNDLE_ACTION_KEY, FullScreenNotificationPermissionDialog.ACTION_GRANT) to { requestFullScreenNotificationPermission() },
            Triple(ExactAlarmPermissionDialog.SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_RESULT_KEY, ExactAlarmPermissionDialog.SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_BUNDLE_ACTION_KEY, ExactAlarmPermissionDialog.ACTION_GRANT) to { requestExactAlarmPermission() },
            Triple(PermissionSettingsDialog.SETTING_DIALOG_RESULT_KEY, PermissionSettingsDialog.SETTING_BUNDLE_ACTION_KEY, PermissionSettingsDialog.ACTION_SETTINGS) to { requestAppSettings() }
        )

        configurations.forEach { (keys, action) ->
            fragmentManager.setFragmentResultListener(keys.first, owner) { _, bundle ->
                if (bundle.getString(keys.second) == keys.third) {
                    action()
                } else {
                    showToastMessage(R.string.permissions_denied)
                }
            }
        }
    }


    //--------------------------------------------------------
    // Handle Permission Launchers Result
    //--------------------------------------------------------
    private fun handlePostNotificationResult(granted: Boolean) {
        when {
            granted -> {
                onPostNotificationGranted()
                showToastMessage(R.string.permission_granted_successfully)
            }
            fragment.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                PermissionRationaleDialog.newInstance(
                    title = context.getString(R.string.notification_permission_required_title),
                    message = context.getString(R.string.post_notification_permission_rationale_message)
                ).show(fragmentManager, PermissionRationaleDialog.TAG)
            }
            else -> {
                PermissionSettingsDialog.newInstance(context.getString(R.string.notification))
                    .show(fragmentManager, PermissionSettingsDialog.TAG)
            }
        }
    }

    private fun handlePermissionCallback(isGranted: Boolean) {
        val message = if (isGranted) R.string.permission_granted_successfully else R.string.permissions_denied
        showToastMessage(message)
    }


    //-------------------
    // Helper Method
    //------------------
    private fun showToastMessage(toastMessageId : Int){
        context.showToast(context.getString(toastMessageId))
    }

}









