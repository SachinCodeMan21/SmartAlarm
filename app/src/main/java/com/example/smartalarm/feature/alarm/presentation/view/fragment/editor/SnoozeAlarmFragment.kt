package com.example.smartalarm.feature.alarm.presentation.view.fragment.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.databinding.FragmentSnoozeAlarmBinding
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorSystemEvent
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * Fragment for managing and configuring snooze settings for an alarm.
 * This fragment provides the UI for enabling/disabling snooze, selecting snooze intervals,
 * and setting a snooze limit. It allows the user to configure their snooze preferences
 * and then save those preferences before navigating back to the previous screen.
 *
 * The fragment handles saving and restoring state for the snooze settings and integrates
 * with the ViewModel to update and persist those settings.
 */
@AndroidEntryPoint
class SnoozeAlarmFragment : Fragment() {


    companion object {

        // Tag for logging within SnoozeAlarmFragment
        private const val TAG = "SnoozeAlarmFragment"

        // Error message for null view binding reference
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // --- State keys for saving/restoring UI state ---
        private const val KEY_SNOOZE_ENABLED = "$PACKAGE.KEY_SNOOZE_ENABLED"
        private const val KEY_SNOOZE_INTERVAL = "$PACKAGE.KEY_SNOOZE_INTERVAL"
        private const val KEY_SNOOZE_LIMIT = "$PACKAGE.KEY_SNOOZE_LIMIT"
    }

    private var _binding: FragmentSnoozeAlarmBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private lateinit var radioButtonPairs: List<Pair<RadioButton, Int>>
    private lateinit var snoozeSettings: SnoozeSettings



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the fragment's state based on the provided arguments or defaults.
     * This ensures that the fragment is correctly configured with either the passed `SnoozeSettings`
     * or the default values, allowing the UI to reflect the intended state when first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: SnoozeAlarmFragmentArgs by navArgs()
        snoozeSettings = args.alarm ?: SnoozeSettings()
    }

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * Inflates the [FragmentSnoozeAlarmBinding] and returns the root view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSnoozeAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Sets up the UI and event handlers after the fragment's view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSnoozeLimitNumPicker()
        setUpRadioButtonPairs()
        setUpSnoozeUIData()
        setUpOnBackPressed()
    }


    /**
     * Saves the current snooze configuration before the fragment is destroyed to handle configuration changes
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SNOOZE_ENABLED, binding.snoozeEnabledSwitch.isChecked)
        outState.putInt(KEY_SNOOZE_INTERVAL, radioButtonPairs.find { it.first.isChecked }?.second ?: 5)
        outState.putInt(KEY_SNOOZE_LIMIT, binding.snoozeLimitPicker.value)
    }

    /**
     *Retrieves the saved snooze settings (enabled status, limit, and interval) from the `savedInstanceState`.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            val restoredEnabled = it.getBoolean(KEY_SNOOZE_ENABLED, snoozeSettings.isSnoozeEnabled)
            val restoredLimit = it.getInt(KEY_SNOOZE_LIMIT, snoozeSettings.snoozeLimit)
            val restoredInterval = it.getInt(KEY_SNOOZE_INTERVAL, snoozeSettings.snoozeIntervalMinutes)

            binding.snoozeEnabledSwitch.isChecked = restoredEnabled
            binding.snoozeLimitPicker.value = restoredLimit
            radioButtonPairs.forEach { (radioBtn, value) ->
                radioBtn.isChecked = value == restoredInterval
            }
        }
    }


    /**
     * Called when the fragment is being destroyed.
     *
     * Cleans up the binding reference to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }




    // ---------------------------------------------------------------------
    // UI SetUp Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the NumberPicker for snooze limit with a custom formatter.
     * The formatter converts the snooze limit value to a localized string representation,
     * without a leading zero, using the `toLocalizedString(false)` function.
     * This ensures that the displayed value follows the user's locale format.
     *
     * @note The formatter is applied to the NumberPicker widget, which will format the value
     *       when displayed in the UI.
     */
    private fun setUpSnoozeLimitNumPicker() = with(binding) {
        snoozeLimitPicker.setFormatter { viewModel.getLocalizedNumber(it,false) }
    }

    /**
     * Initializes the list of radio button and snooze interval pairs.
     *
     * Each pair links a radio button in the UI to a specific snooze interval in minutes.
     * This allows the user to select a predefined snooze duration for the alarm.
     *
     * Example:
     * - Selecting `radio10min` sets the snooze interval to 10 minutes.
     */
    private fun setUpRadioButtonPairs() = with(binding) {
        radioButtonPairs = listOf(
            radio1min to 1,
            radio5min to 5,
            radio10min to 10,
            radio15min to 15,
            radio20min to 20,
            radio25min to 25,
            radio30min to 30
        )
    }

    /**
     * Initializes snooze UI elements with current snooze settings.
     */
    private fun setUpSnoozeUIData() = with(binding) {
        snoozeEnabledSwitch.isChecked = snoozeSettings.isSnoozeEnabled
        snoozeLimitPicker.value = snoozeSettings.snoozeLimit
        radioButtonPairs.forEach { (radioBtn, value) ->
            radioBtn.isChecked = (value == snoozeSettings.snoozeIntervalMinutes)
        }
    }

    /**
     * Sets up custom back press behavior to send result and navigate back.
     */
    private fun setUpOnBackPressed() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    sendResultAndNavigateBack()
                }
            }
        )
    }


    // ---------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------

    /**
     * Handles toolbar back button press by sending result and navigating back.
     */
    fun onToolbarBackPressed() {
        sendResultAndNavigateBack()
    }

    /**
     *
     * This method is used to save the user's snooze preferences and exit the current screen,
     * ensuring that the UI and ViewModel are kept in sync with the updated settings.
     */
    private fun sendResultAndNavigateBack() {

        val snoozeSettings = SnoozeSettings(
            isSnoozeEnabled = binding.snoozeEnabledSwitch.isChecked,
            snoozeIntervalMinutes = radioButtonPairs.find { it.first.isChecked }?.second ?: 5,
            snoozeLimit = binding.snoozeLimitPicker.value,
            snoozedCount = binding.snoozeLimitPicker.value
        )

        viewModel.handleSystemEvent(AlarmEditorSystemEvent.SnoozeUpdated(snoozeSettings))
        findNavController().popBackStack()

    }



}