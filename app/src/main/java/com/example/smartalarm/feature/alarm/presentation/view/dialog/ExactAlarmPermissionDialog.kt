package com.example.smartalarm.feature.alarm.presentation.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class ExactAlarmPermissionDialog : DialogFragment() {


    companion object {
        const val REQUEST_KEY = "exact_alarm_permission_request"
        const val KEY_RESULT = "exact_alarm_permission_granted"

        fun show(fragmentManager: FragmentManager) {
            ExactAlarmPermissionDialog().show(fragmentManager, "ExactAlarmPermissionDialog")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("To schedule alarms reliably, please allow exact alarm permission.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Notify host with positive result
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(KEY_RESULT to true)
                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Notify host with negative result
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(KEY_RESULT to false)
                )
            }
            .create()
    }

}

