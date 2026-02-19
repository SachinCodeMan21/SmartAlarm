package com.example.smartalarm.feature.home.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.PACKAGE

class ExactAlarmPermissionDialog : DialogFragment() {

    companion object {

        /** Tag for the dialog fragment, used for fragment transactions */
        const val TAG = "$PACKAGE.schedule_exact_alarm_permission_dialog_tag"

        /** Request key used for communicating results via Fragment Result API */
        const val SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_RESULT_KEY = "$PACKAGE.schedule_exact_alarm_permission_dialog_result_key"

        /** Bundle key to identify the user's action in the result bundle */
        const val SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_BUNDLE_ACTION_KEY = "$PACKAGE.schedule_exact_alarm_permission_dialog_bundle"

        /** Argument key for dialog title */
        const val ARG_TITLE = "$PACKAGE.schedule_exact_alarm_dialog.arg_title"

        /** Argument key for dialog message */
        const val ARG_MESSAGE = "$PACKAGE.schedule_exact_alarm_dialog.arg_message"

        /** User action string representing permission grant */
        const val ACTION_GRANT = "$PACKAGE.schedule_exact_alarm_dialog.grant"

        /** User action string representing permission denial */
        const val ACTION_DENY = "$PACKAGE.schedule_exact_alarm_dialog.deny"

        /**
         * Creates a new instance of [ExactAlarmPermissionDialog] with the provided
         * [title] and [message].
         *
         * @param title The dialog title to display.
         * @param message The dialog message explaining the schedule exact alarm permission rationale.
         * @return A configured instance of [ExactAlarmPermissionDialog].
         */
        fun newInstance(title: String, message: String): ExactAlarmPermissionDialog {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
            return ExactAlarmPermissionDialog().apply {
                arguments = args
            }
        }
    }

    /**
     * Creates and returns a dialog that informs the user about the schedule exact alarm permission,
     * allowing them to either grant or deny the requested permission. The dialog provides two options:
     *
     * - **Grant Permission**: Sends a "grant" action back to the listener, prompting the user to go to settings
     *   and enable the schedule exact alarm permission.
     * - **Deny**: Sends a "deny" action back to the listener, indicating that the user has declined to proceed.
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
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                // Send "grant" action back to listener
                setFragmentResult(
                    SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_RESULT_KEY,
                    bundleOf(SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_BUNDLE_ACTION_KEY to ACTION_GRANT)
                )
            }
            .setNegativeButton(getString(R.string.deny)) { _, _ ->
                // Send "deny" action back to listener
                setFragmentResult(
                    SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_RESULT_KEY,
                    bundleOf(SCHEDULE_EXACT_ALARM_PERMISSION_DIALOG_BUNDLE_ACTION_KEY to ACTION_DENY)
                )
            }
            .setCancelable(true)
            .create()
    }
}
