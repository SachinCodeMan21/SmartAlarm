package com.example.smartalarm.core.permission.model

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.R
import com.example.smartalarm.core.permission.PermissionChecker
import com.example.smartalarm.core.permission.PermissionRequester
import com.example.smartalarm.core.permission.onRationaleResult
import com.example.smartalarm.core.permission.onSettingsResult
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
            coordinateSinglePermission(current, activity) { isGranted ->
                results[current] = isGranted
                processNext(index + 1) // Only moves to next when UI is dismissed
            }
        }

        processNext(0)

    }

    private fun coordinateSinglePermission(
        permission: AppPermission,
        activity: Activity,
        onDone: (Boolean) -> Unit
    ) {
        val status = checker.checkPermissionStatus(permission, activity)

        when (status) {

            is PermissionStatus.RuntimePermissionStatus.ShouldShowRationale -> {
                // Wait for DialogFragment Listener
                showRationaleDialog(
                    permission,
                    onConfirm = { executeSystemRequest(permission, onDone) },
                    onCancel = { onDone(false) }
                )
            }

            else -> executeSystemRequest(permission, onDone)
        }
    }

    private fun executeSystemRequest(
        permission: AppPermission,
        onDone: (Boolean) -> Unit
    ) {
        requester.requestSinglePermission(permission) { result ->

            when (result) {
                is PermissionResult.RuntimePermissionResult.Granted,
                is PermissionResult.SpecialPermissionResult.Granted -> {
                    onDone(true)
                }

                is PermissionResult.RuntimePermissionResult.PermanentlyDenied -> {

                    // Wait for DialogFragment Listener
                    showSettingsDialog(
                        permission,
                        onSettingsClicked = {
                            requester.requestSinglePermission(permission) { finalResult ->
                                val isGranted = finalResult is PermissionResult.SpecialPermissionResult.Granted || finalResult is PermissionResult.RuntimePermissionResult.Granted
                                onDone(isGranted)
                            }
                        },
                        onCancelClicked = { onDone(false) }
                    )
                }

                else -> onDone(false)
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
        PermissionRationaleDialog.newInstance(
            title = context.getString(
                permission.rationaleTitleResId ?: R.string.permission_required_title
            ),
            message = context.getString(
                permission.rationaleMessageResId
                    ?: R.string.post_notification_permission_rationale_message
            )
        ).show(fragmentManager, PermissionRationaleDialog.TAG)
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
        PermissionSettingsDialog.newInstance(
            permissionName = context.getString(permission.friendlyNameResId)
        ).show(fragmentManager, PermissionSettingsDialog.TAG)
    }

}