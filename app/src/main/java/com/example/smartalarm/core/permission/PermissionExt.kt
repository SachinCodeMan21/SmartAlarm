package com.example.smartalarm.core.permission

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.feature.home.presentation.dialog.PermissionRationaleDialog
import com.example.smartalarm.feature.home.presentation.dialog.PermissionSettingsDialog

fun FragmentActivity.createPermissionRequester(checker: PermissionChecker) =
    PermissionRequester(
        caller = this,
        lifecycleOwner = this,
        context = this,
        permissionChecker = checker,
        rationaleProvider = { shouldShowRequestPermissionRationale(it) }
    )

fun Fragment.createPermissionRequester(checker: PermissionChecker) =
    PermissionRequester(
        caller = this,
        lifecycleOwner = viewLifecycleOwner, // Crucial for Fragments!
        context = requireContext(),
        permissionChecker = checker,
        rationaleProvider = { shouldShowRequestPermissionRationale(it) }
    )


/**
 * Extension for the Rationale Dialog to send results back to the Coordinator
 */
fun PermissionRationaleDialog.sendResult(action: String) {
    setFragmentResult(
        PermissionRationaleDialog.RATIONALE_DIALOG_KEY,
        bundleOf(PermissionRationaleDialog.RATIONALE_DIALOG_ACTION_KEY to action)
    )
}

/**
 * Extension to listen for Rationale Dialog results via Fragment Result API.
 */
fun FragmentManager.onRationaleResult(
    lifecycleOwner: LifecycleOwner,
    onGrantClicked: () -> Unit,
    onDenyClicked: () -> Unit
) {
    this.setFragmentResultListener(
        PermissionRationaleDialog.RATIONALE_DIALOG_KEY,
        lifecycleOwner
    ) { _, bundle ->
        val action = bundle.getString(PermissionRationaleDialog.RATIONALE_DIALOG_ACTION_KEY)

        // We evaluate the bundle and map it to our clean callbacks
        if (action == PermissionRationaleDialog.ACTION_GRANT) {
            onGrantClicked()
        } else {
            onDenyClicked()
        }
    }
}

/**
 * Extension for the Settings Dialog to send results back to the Coordinator
 */
fun PermissionSettingsDialog.sendResult(action: String) {
    setFragmentResult(
        PermissionSettingsDialog.SETTING_DIALOG_RESULT_KEY,
        bundleOf(PermissionSettingsDialog.SETTING_BUNDLE_ACTION_KEY to action)
    )
}

fun FragmentManager.onSettingsResult(
    lifecycleOwner: LifecycleOwner,
    onSettingsClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    this.setFragmentResultListener(
        PermissionSettingsDialog.SETTING_DIALOG_RESULT_KEY,
        lifecycleOwner
    ) { _, bundle ->
        val action = bundle.getString(PermissionSettingsDialog.SETTING_BUNDLE_ACTION_KEY)
        if (action == PermissionSettingsDialog.ACTION_SETTINGS) {
            onSettingsClicked()
        } else {
            onCancelClicked()
        }
    }
}