package com.example.smartalarm.feature.home.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.PACKAGE

/**
 * A [DialogFragment] that informs the user a permission has been permanently denied
 * (i.e., "Don't ask again" was selected), and guides them to manually enable
 * the permission through the app's system settings.
 *
 * The dialog displays:
 * - A message indicating which permission is missing.
 * - A "Settings" button that triggers navigation to the app's system settings screen.
 * - A "Cancel" button to dismiss the dialog.
 *
 * The dialog uses the Fragment Result API to notify the host of the user's choice:
 * - If the user taps "Settings", the fragment result with key [SETTING_DIALOG_RESULT_KEY] and
 *   value [ACTION_SETTINGS] is sent.
 * - If the user taps "Cancel", the fragment result with key [SETTING_DIALOG_RESULT_KEY] and
 *   value [ACTION_DENY] is sent.
 *
 * This allows the host (Activity or Fragment) to handle the user's response appropriately.
 *
 * */
class PermissionSettingsDialog : DialogFragment() {

    companion object {

        const val TAG = "permission_denied_dialog_tag"

        // KEYS
        const val SETTING_DIALOG_RESULT_KEY = "permission_denied_request_key"
        const val SETTING_BUNDLE_ACTION_KEY = "permission_denied_action"


        // REQUIRED ARGUMENTS
        const val ARG_PERMISSION_NAME = "$PACKAGE.settings.dialog.arg_permission_name"


        // ACTION STRINGS EXTRAS
        const val ACTION_SETTINGS = "$PACKAGE.settings.dialog.settings"
        const val ACTION_DENY = "$PACKAGE.settings.dialog.deny"


        /**
         * Creates a new instance of the `PermissionSettingsDialog` and passes the given permission name
         * as an argument to the dialog fragment.
         *
         * This method is used to create and initialize a `PermissionSettingsDialog` with a specific
         * permission name, which can be used to show relevant information or perform actions related
         * to the specified permission.
         *
         * @param permissionName The name of the permission that the dialog will handle.
         *
         * @return A new instance of `PermissionSettingsDialog` with the permission name set as an argument.
         */
        fun newInstance(permissionName: String): PermissionSettingsDialog {
            val args = Bundle().apply {
                putString(ARG_PERMISSION_NAME, permissionName)
            }
            return PermissionSettingsDialog().apply {
                arguments = args
            }
        }
    }

    /**
     * Creates and returns a dialog that informs the user about the required permission
     * and provides options to either open the app settings or cancel the action.
     *
     * This dialog displays a message requesting the user to review and grant the necessary permission.
     * The dialog provides two options:
     * - **Settings**: Navigates the user to the app's settings page to allow the user to grant the permission.
     * - **Cancel**: Closes the dialog without making any changes, effectively denying the action.
     *
     * The appropriate result is sent via the `setFragmentResult` method, indicating whether the user
     * chose to go to the settings or cancel the operation.
     *
     * @param savedInstanceState The saved state of the dialog, if any.
     *
     * @return The created `AlertDialog` instance for displaying the permission request.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val permissionName = requireArguments().getString(ARG_PERMISSION_NAME)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required_title))
            .setMessage(getString(R.string.app_permission_setting_dialog_message, permissionName))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                setFragmentResult(
                    SETTING_DIALOG_RESULT_KEY,
                    bundleOf(SETTING_BUNDLE_ACTION_KEY to ACTION_SETTINGS)
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                setFragmentResult(
                    SETTING_DIALOG_RESULT_KEY,
                    bundleOf(SETTING_BUNDLE_ACTION_KEY to ACTION_DENY)
                )
            }
            .create()
    }

}

