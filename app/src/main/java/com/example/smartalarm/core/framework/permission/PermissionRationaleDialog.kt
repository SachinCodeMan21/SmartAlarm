package com.example.smartalarm.core.framework.permission

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * PERMISSION RATIONALE DIALOG
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * A reusable DialogFragment that explains WHY a permission is needed.
 * Show this in the `PermissionState.Denied` case BEFORE re-requesting,
 * or in the `PermissionState.PermanentlyDenied` case to send users to Settings.
 *
 * HOW TO USE:
 *
 *   // Case 1: Denied (can ask again)
 *   PermissionRationaleDialog.showRationale(
 *       fragmentManager = supportFragmentManager,
 *       title           = "Camera Access Needed",
 *       message         = "We need camera access to let you scan QR codes.",
 *       positiveText    = "Allow",
 *       negativeText    = "Not Now",
 *       onPositive      = { permissionManager.request(AppPermission.Camera) { ... } },
 *       onNegative      = { /* user dismissed */ }
 *   )
 *
 *   // Case 2: Permanently Denied (must go to Settings)
 *   PermissionRationaleDialog.showGoToSettings(
 *       fragmentManager = supportFragmentManager,
 *       title           = "Camera Permission Blocked",
 *       message         = "You've permanently denied camera access. Enable it in Settings.",
 *       onPositive      = { permissionManager.openAppSettings() }
 *   )
 */

class PermissionRationaleDialog : DialogFragment() {

    // Using interface callbacks instead of lambdas in Bundle (lambdas aren't Parcelable)
    var onPositive: (() -> Unit)? = null
    var onNegative: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        return AlertDialog.Builder(requireContext())
            .setTitle(args.getString(ARG_TITLE))
            .setMessage(args.getString(ARG_MESSAGE))
            .setPositiveButton(args.getString(ARG_POSITIVE)) { _, _ -> onPositive?.invoke() }
            .setNegativeButton(args.getString(ARG_NEGATIVE)) { _, _ -> onNegative?.invoke() }
            .create()
    }

    companion object {
        private const val TAG = "PermissionRationaleDialog"
        private const val ARG_TITLE    = "title"
        private const val ARG_MESSAGE  = "message"
        private const val ARG_POSITIVE = "positive"
        private const val ARG_NEGATIVE = "negative"

        /** Show rationale before re-requesting a denied permission. */
        fun showRationale(
            fragmentManager : FragmentManager,
            title           : String,
            message         : String,
            positiveText    : String = "Allow",
            negativeText    : String = "Not Now",
            onPositive      : () -> Unit,
            onNegative      : () -> Unit = {}
        ) = show(fragmentManager, title, message, positiveText, negativeText, onPositive, onNegative)

        /** Show dialog to send the user to App Settings (permanently denied case). */
        fun showGoToSettings(
            fragmentManager : FragmentManager,
            title           : String = "Permission Required",
            message         : String,
            onPositive      : () -> Unit,
            onNegative      : () -> Unit = {}
        ) = show(fragmentManager, title, message, "Open Settings", "Cancel", onPositive, onNegative)

        private fun show(
            fragmentManager : FragmentManager,
            title           : String,
            message         : String,
            positiveText    : String,
            negativeText    : String,
            onPositive      : () -> Unit,
            onNegative      : () -> Unit
        ) {
            // Avoid showing twice if already visible (e.g. orientation change)
            if (fragmentManager.findFragmentByTag(TAG) != null) return

            PermissionRationaleDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE,    title)
                    putString(ARG_MESSAGE,  message)
                    putString(ARG_POSITIVE, positiveText)
                    putString(ARG_NEGATIVE, negativeText)
                }
                this.onPositive = onPositive
                this.onNegative = onNegative
            }.show(fragmentManager, TAG)
        }
    }
}