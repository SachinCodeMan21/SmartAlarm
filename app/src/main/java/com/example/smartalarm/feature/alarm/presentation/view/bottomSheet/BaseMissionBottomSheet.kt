package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A base class for a bottom sheet dialog fragment that handles mission-related UI interactions.
 *
 * This fragment is responsible for displaying a mission-related UI and managing the bottom sheet's behavior,
 * including expanding the sheet to a specific height and managing its state.
 *
 * The class defines essential constants used for passing mission-related arguments to the bottom sheet,
 * such as the mission data and the number of rounds.
 */
abstract class BaseMissionBottomSheet : BottomSheetDialogFragment() {

    companion object{

        // Tag used for logging within BaseMissionBottomSheet for debugging purposes
        const val TAG = "$PACKAGE.BaseMissionBottomSheet"

        // Key used to pass the mission arguments to the BaseMissionBottomSheet
        const val PASSED_MISSION_ARGS_KEY = "$PACKAGE.BaseMissionBottomSheet_PASSED_MISSION_ARGS_KEY"

        // Key used to store or retrieve the rounds value from the BaseMissionBottomSheet
        const val ROUNDS_VALUE_KEY = "$PACKAGE.KEY_ROUNDS_VALUE"

        // Error message for when the passed mission data to the Bottom Sheet is missing or null
        const val PASSED_MISSION_ARGS_NULL = "Passed Mission To Mission Bottom Sheet Is Missing or Null"


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    /**
     * Helper to apply bottom system insets to any view.
     * Call this from onViewCreated in subclasses.
     */
    protected fun applyBottomSystemInset(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(rootView)
    }
}
