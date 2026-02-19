package com.example.smartalarm.feature.timer.presentation.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartalarm.R
import android.view.LayoutInflater
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.showSnackBar
import com.example.smartalarm.databinding.FragmentTimerBinding
import com.example.smartalarm.feature.timer.presentation.effect.TimerEffect
import com.example.smartalarm.feature.timer.presentation.event.TimerEvent
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import com.example.smartalarm.feature.timer.presentation.viewmodel.TimerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


/**
 * [TimerFragment] is responsible for managing the UI and user interactions for the timer screen.
 * It handles user input via a dynamic keypad, controls for starting a timer, and navigating to
 * a list of existing timers. This fragment observes [TimerViewModel] for reactive UI updates and effects.
 *
 * ## Responsibilities:
 * - Inflate and bind the timer layout using ViewBinding.
 * - Configure a dynamic keypad for timer input.
 * - Handle user actions such as starting or deleting a timer.
 * - Observe UI state (`uiState`) and one-time effects (`uiEffect`) from the ViewModel.
 * - Navigate to other screens based on received effects (e.g., `ShowTimerActivity`).
 *
 * ## Architecture:
 * - MVVM-based: Communicates with [TimerViewModel] for logic and state management.
 * - Lifecycle-aware: Uses `repeatOnLifecycle` and `lifecycleScope` to safely collect state and effects.
 * - Hilt-enabled: Annotated with `@AndroidEntryPoint` for dependency injection.
 *
 * @see TimerViewModel
 * @see TimerEvent
 * @see TimerEffect
 */
@AndroidEntryPoint
class TimerFragment : Fragment() {

    companion object {

        // Tag used for logging within TimerFragment
        private const val TAG = "TimerFragment"

        // Error message when the view binding is null in TimerFragment
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
    }

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding?: error(BINDING_NULL_ERROR)
    private val timerViewModel: TimerViewModel by viewModels()


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called when the fragment’s UI is being created.
     *
     * Inflates the layout and initializes ViewBinding for the timer fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Called immediately after the fragment's view has been created.
     *
     * Sets up dynamic keypad input, button listeners, and observers for UI state and effects.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpKeypad()
        setUpButtonListeners()
        setUpUiStateObserver()
        setUpUiEffectObserver()
    }

    /**
     * Initializes the timer UI state when the  fragment starts,
     * ensuring the UI reflects the current timer data.
     */
    override fun onStart() {
        super.onStart()
        timerViewModel.handleEvent(TimerEvent.InitTimerUIState)
    }

    /**
     * Called when the fragment's view is about to be destroyed.
     *
     * Clears the binding reference to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes and configures the static keypad using predefined MaterialButtons from the layout.
     *
     * Responsibilities:
     * 1. Maps each keypad button (0–9, 00, ⌫) to its corresponding label.
     * 2. Assigns a click listener to each button.
     * 3. On button click, dispatches a `TimerEvent.HandleKeypadClick(label)` event to the ViewModel.
     *
     * This method replaces the previously used dynamic keypad view and assumes
     * all button views are correctly initialized via ViewBinding.
     */
    private fun setUpKeypad() = with(binding) {

        val numberButtons = listOf(
            btnZero to R.string._0,
            btnDoubleZero to R.string._00,
            btnOne to R.string._1,
            btnTwo to R.string._2,
            btnThree to R.string._3,
            btnFour to R.string._4,
            btnFive to R.string._5,
            btnSix to R.string._6,
            btnSeven to R.string._7,
            btnEight to R.string._8,
            btnNine to R.string._9,
            btnBackspace to R.string.btnBackSpace
        )

        numberButtons.forEach { (button, resId) ->
            button.setOnClickListener {
                timerViewModel.handleEvent(TimerEvent.HandleKeypadClick(getString(resId)))
            }
        }
    }



    /**
     * Sets up click listeners for timer control buttons.
     *
     * - Starts the timer when the start button is pressed.
     * - Navigates to the show timers list screen via the delete FAB, allowing the user to remove an existing timer.
     */
    private fun setUpButtonListeners() = binding.apply {
        startTimerBtn.setOnClickListener {
            timerViewModel.handleEvent(TimerEvent.HandleStartTimerClick)
        }
        deleteTimerFabBtn.setOnClickListener {
            timerViewModel.handleEvent(TimerEvent.HandleDeleteTimerClick)
        }
    }


    // ---------------------------------------------------------------------
    // UI State & Effect Observer Methods
    // ---------------------------------------------------------------------

    /**
     * Observes UI state updates from the [TimerViewModel] and updates the timer screen accordingly.
     *
     * - Updates the timer text with the formatted time.
     * - Toggles visibility of the start button based on the current state.
     * - Shows or hides the delete FAB if there are running timers.
     */
    private fun setUpUiStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timerViewModel.uiState.collectLatest { uiState ->
                    binding.apply {
                        timerTextTv.text = uiState.formattedTime
                        startTimerBtn.isVisible = uiState.isStartButtonVisible
                        startTimerBtn.visibility = if (uiState.isStartButtonVisible) View.VISIBLE else View.INVISIBLE
                        deleteTimerFabBtn.isVisible = uiState.isDeleteTimerButtonVisible
                    }
                }
            }
        }
    }


    /**
     * Observes one-time UI effects from the [TimerViewModel] and performs corresponding UI actions.
     *
     * - Navigates to the show timers list screen when [TimerEffect.NavigateToShowTimerScreen] is received.
     * - Displays a SnackBar with a message when [TimerEffect.ShowSnackBar] is received.
     */
    private fun setUpUiEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timerViewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is TimerEffect.NavigateToShowTimerScreen -> startActivity(Intent(requireContext(), ShowTimerActivity::class.java))
                        is TimerEffect.ShowSnackBar -> binding.root.showSnackBar(effect.message, Snackbar.LENGTH_SHORT)
                    }
                }
            }
        }
    }


}