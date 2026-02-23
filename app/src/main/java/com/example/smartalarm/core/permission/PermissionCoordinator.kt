package com.example.smartalarm.core.permission

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.R
import com.example.smartalarm.core.permission.model.AppFeature
import com.example.smartalarm.core.permission.model.AppPermission
import com.example.smartalarm.core.permission.model.PermissionResult
import com.example.smartalarm.core.permission.model.PermissionStatus
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.feature.home.presentation.dialog.PermissionRationaleDialog
import com.example.smartalarm.feature.home.presentation.dialog.PermissionSettingsDialog

class PermissionCoordinator(
    private val context: Context,
    private val requester: PermissionRequester,
    private val checker: PermissionChecker,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) {

    /**
     * Entry point for the ViewModel/Fragment.
     * Processes a list of permissions one by one.
     */
    fun runPermissionGatekeeper(
        permissions: List<AppPermission>,
        activity: Activity,
        feature: AppFeature,
        onFinished: (Map<AppPermission, Boolean>) -> Unit
    ) {

        val results = mutableMapOf<AppPermission, Boolean>()

        // Internal recursive function to process the chain
        fun processNext(index: Int) {

            if (index >= permissions.size) {
                onFinished(results)
                return
            }

            val current = permissions[index]

            // If already granted, skip to next immediately
            if (checker.isGranted(current)) {
                results[current] = true
                processNext(index + 1)
                return
            }

            // Otherwise, start the UI/Request flow for this permission
            coordinateSinglePermission(current, activity, feature) { isGranted ->
                results[current] = isGranted
                processNext(index + 1) // Only moves to next when UI is dismissed
            }
        }

        processNext(0)

    }

    private fun coordinateSinglePermission(
        permission: AppPermission,
        activity: Activity,
        feature: AppFeature,
        onDone: (Boolean) -> Unit
    ) {
        val status = checker.checkPermissionStatus(permission, activity)

        when (status) {

            is PermissionStatus.RuntimePermissionStatus.ShouldShowRationale -> {
                // Wait for DialogFragment Listener
                showRationaleDialog(
                    permission,
                    onConfirm = { executeSystemRequest(permission, feature,onDone) },
                    onCancel = { onDone(false) }
                )
            }

            else -> executeSystemRequest(permission, feature,onDone)
        }
    }

    private fun executeSystemRequest(
        permission: AppPermission,
        featureName: AppFeature,
        onDone: (Boolean) -> Unit
    ) {
        requester.requestSinglePermission(permission) { result ->

            when (result) {

                is PermissionResult.RuntimePermissionResult.Granted -> {
                    context.showToast(context.getString(R.string.permission_granted_successfully))
                    onDone(true)
                }
                is PermissionResult.RuntimePermissionResult.Denied -> {
                    showRationaleDialog(
                        permission,
                        onConfirm = { executeSystemRequest(permission, featureName,onDone) },
                        onCancel = { onDone(false) }
                    )
                }
                is PermissionResult.RuntimePermissionResult.PermanentlyDenied -> {

                    // Check if we've already shown this for the current feature
                    val permissionKey = permission.toString()

                    if (PermissionSessionTracker.hasBeenShown(featureName, permissionKey)) {
                        // SILENT DENIAL: We've already shown the dialog this session
                        onDone(false)
                    } else {
                        // Show dialog and mark as shown
                        showSettingsDialog(
                            permission,
                            onSettingsClicked = {
                                requester.openAppSettings(permission) {
                                    onDone(checker.isGranted(permission))
                                }
                            },
                            onCancelClicked = {
                                // Even if they cancel, mark it as shown so we don't ask again
                                PermissionSessionTracker.markAsShown(featureName, permissionKey)
                                onDone(false)
                            }
                        )
                    }
                }

                is PermissionResult.SpecialPermissionResult.Granted,
                is PermissionResult.SpecialPermissionResult.Denied -> onDone(true)

            }
        }
    }





    // --- UI Methods (To be implemented by your DialogFragment logic) ---

    private fun showRationaleDialog(
        permission: AppPermission,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        // 1. Setup the Receiver Extension
        fragmentManager.onRationaleResult(
            lifecycleOwner = lifecycleOwner,
            onGrantClicked = { onConfirm() },
            onDenyClicked = { onCancel() }
        )

        // 2. Show the Dialog
        PermissionRationaleDialog.Companion.newInstance(
            title = context.getString(
                permission.defaultRationaleTitleResId
            ),
            message = context.getString(
                permission.defaultRationaleMessageResId
            )
        ).show(fragmentManager, PermissionRationaleDialog.Companion.TAG)
    }


    private fun showSettingsDialog(
        permission: AppPermission,
        onSettingsClicked: () -> Unit,
        onCancelClicked: () -> Unit
    ) {

        // 1. Setup the Receiver Extension
        fragmentManager.onSettingsResult(
            lifecycleOwner = lifecycleOwner,
            onSettingsClicked = {
                onSettingsClicked()
            },
            onCancelClicked = {
                onCancelClicked()
            }
        )

        // 2. Show the Dialog
        PermissionSettingsDialog.Companion.newInstance(
            permissionName = context.getString(permission.labelResId)
        ).show(fragmentManager, PermissionSettingsDialog.Companion.TAG)
    }

}