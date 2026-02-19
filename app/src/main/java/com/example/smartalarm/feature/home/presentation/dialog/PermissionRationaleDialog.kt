package com.example.smartalarm.feature.home.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.PACKAGE

/**
 * A [DialogFragment] that shows a rationale dialog explaining why a permission is needed.
 *
 * The dialog displays a customizable title and message, and provides two actions:
 * - Grant permission
 * - Deny permission
 *
 * Instead of using callbacks or lambdas directly, this dialog uses the Fragment Result API
 * to communicate the user's choice back to the caller, ensuring safe handling across
 * configuration changes such as device rotation.
 *
 * ### Usage:
 * - Create an instance with [newInstance], providing title and message strings.
 * - Show the dialog via [show].
 * - Listen for the result with [androidx.fragment.app.setFragmentResultListener] using [RATIONALE_DIALOG_KEY].
 * - The result bundle will contain [RATIONALE_DIALOG_ACTION_KEY] with either [ACTION_GRANT] or [ACTION_DENY].
 */
class PermissionRationaleDialog : DialogFragment() {

    companion object {

        /** Tag for the dialog fragment, used for fragment transactions */
        const val TAG = "$PACKAGE.permission_rationale_dialog_tag"

        // KEYS

        /** Request key used for communicating results via Fragment Result API */
        const val RATIONALE_DIALOG_KEY = "$PACKAGE.permission_rationale_result_key"

        /** Bundle key to identify the user's action in the result bundle */
        const val RATIONALE_DIALOG_ACTION_KEY = "$PACKAGE.permission_rationale_dialog_bundle"


        // REQUIRED ARGUMENTS

        /** Argument key for dialog title */
        const val ARG_TITLE = "$PACKAGE.rationale.dialog.arg_title"

        /** Argument key for dialog message */
        const val ARG_MESSAGE = "$PACKAGE.rationale.dialog.arg_message"


        // ACTION STRINGS EXTRAS

        /** User action string representing permission grant */
        const val ACTION_GRANT = "$PACKAGE.rationale.dialog.grant"

        /** User action string representing permission denial */
        const val ACTION_DENY = "$PACKAGE.rationale.dialog.deny"


        /**
         * Creates a new instance of [PermissionRationaleDialog] with the provided
         * [title] and [message].
         *
         * @param title The dialog title to display.
         * @param message The dialog message explaining the permission rationale.
         * @return A configured instance of [PermissionRationaleDialog].
         */
        fun newInstance(title: String, message: String): PermissionRationaleDialog {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
            return PermissionRationaleDialog().apply {
                arguments = args
            }
        }
    }

    /**
     * Creates and returns a dialog with customizable title and message, allowing the user
     * to either grant or deny the requested permission. The dialog provides two options:
     *
     * - **Grant Permission**: Sends a "grant" action back to the listener indicating the user has
     *   accepted the permission request.
     * - **Deny**: Sends a "deny" action back to the listener indicating the user has declined the request.
     *
     * The title and message of the dialog are passed as arguments to the fragment, making the
     * dialog reusable in different contexts. The result is returned via `setFragmentResult` to notify
     * the caller of the user's decision.
     *
     * @param savedInstanceState A `Bundle` containing the saved state of the dialog, if any.
     *
     * @return The created `AlertDialog` instance, ready to be shown to the user.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val title = requireArguments().getString(ARG_TITLE)
        val message = requireArguments().getString(ARG_MESSAGE)

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                // Send "grant" action back to listener
                setFragmentResult(
                    RATIONALE_DIALOG_KEY,
                    bundleOf(RATIONALE_DIALOG_ACTION_KEY to ACTION_GRANT)
                )
            }
            .setNegativeButton(getString(R.string.deny)) { _, _ ->
                // Send "deny" action back to listener
                setFragmentResult(
                    RATIONALE_DIALOG_KEY,
                    bundleOf(RATIONALE_DIALOG_ACTION_KEY to ACTION_DENY)
                )
            }
            .setCancelable(true)
            .create()
    }

}

