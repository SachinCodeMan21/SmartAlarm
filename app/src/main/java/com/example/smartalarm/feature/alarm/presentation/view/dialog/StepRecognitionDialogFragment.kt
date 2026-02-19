package com.example.smartalarm.feature.alarm.presentation.view.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.smartalarm.R

class StepRecognitionDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_step_recognition_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Adjust dialog size here
        dialog?.window?.apply {
            // Set width and height
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,  // Width to match the screen
                WindowManager.LayoutParams.WRAP_CONTENT   // Height to wrap content
            )
            // Optional: set gravity to center the dialog on the screen
            setGravity(Gravity.CENTER)
        }
    }

    companion object {
        fun newInstance(): StepRecognitionDialogFragment {
            return StepRecognitionDialogFragment()
        }
    }
}
