package com.example.smartalarm.feature.stopwatch.presentation.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.databinding.FragmentStopwatchBinding
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.presentation.adapter.StopWatchLapAdapter
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.presentation.viewmodel.StopWatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Fragment responsible for presenting and managing the stopwatch feature.
 *
 * Acts as the **UI layer in an MVVM setup**:
 * - Renders state from [StopWatchViewModel].
 * - Forwards user interactions as [StopwatchEvent]s to the ViewModel.
 * - Delegates long-running behavior to background services when the app is not visible.
 *
 * UI logic is **lifecycle-aware** to avoid unnecessary work and resource leaks,
 * ensuring proper cleanup when the fragment is destroyed.
 */
@AndroidEntryPoint
class StopwatchFragment : Fragment() {


    companion object {

        /** Tag used for logging within [StopwatchFragment]. */
        private const val TAG = "StopWatchFragment"

        /** Error message thrown when view binding is unexpectedly null. */
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

    }


    /** View binding instance for this fragment. */
    private var _binding: FragmentStopwatchBinding? = null

    /** Non-null accessor for view binding, throws if null. */
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)

    /** ViewModel controlling stopwatch state and events. */
    private val stopWatchViewModel: StopWatchViewModel by viewModels()

    /** Animator handling dynamic layout changes and transitions for the stopwatch UI. */
    private var stopWatchAnimator: StopwatchLayoutAnimator? = null

    /** Stores the previous number of recorded laps to manage animations and scrolling. */
    private var previousLapsCount = 0

    /** Adapter for displaying stopwatch lap times in a RecyclerView  */
    private lateinit var stopWatchLapAdapter: StopWatchLapAdapter

    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Inflates and returns the stopwatch fragment layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Sets up the UI after the view has been created.
     *
     * Sets up:
     * - Progress bar animator
     * - Lap RecyclerView
     * - Button click listeners
     * - Lifecycle-aware UI state and effect observers
     * - Permission handlers
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeProgressBarAnimator()
        setUpButtonClickListeners()
        setUpLapRecyclerView()
        setUpUIStateObserver()
        setUpUIEffectObserver()
    }


    /**
     * Hands off stopwatch execution to a foreground service when the UI is no longer visible.
     *
     * Executed only if notification permission is granted.
     * Skipped during configuration changes to avoid duplicate service starts.
     */
    override fun onStop() {
        super.onStop()
        stopWatchViewModel.handleEvent(StopwatchEvent.MoveToBackground)
    }


    /**
     * Cleans up view-related resources to avoid memory leaks.
     *
     * Clears the view binding and animator when the fragment's view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        stopWatchAnimator = null
        _binding = null
    }


    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------

    /** Initializes the animator responsible for dynamic stopwatch layout adjustments. */
    private fun initializeProgressBarAnimator() {
        stopWatchAnimator = StopwatchLayoutAnimator(binding)
    }


    /**
     * Connects UI buttons to corresponding [StopwatchEvent]s in the ViewModel.
     *
     * Ensures that all button actions are handled consistently and the fragment
     * delegates execution to the ViewModel.
     */
    private fun setUpButtonClickListeners() = with(binding) {
        resetStopwatchBtn.setOnClickListener {
            stopWatchViewModel.handleEvent(StopwatchEvent.ResetStopwatch)
        }
        toggleStopwatchBtn.setOnClickListener {
            stopWatchViewModel.handleEvent(StopwatchEvent.ToggleRunState)
        }
        recordLapStopwatchBtn.setOnClickListener {
            stopWatchViewModel.handleEvent(StopwatchEvent.RecordStopwatchLap)
        }
    }


    /**
     * Prepares the RecyclerView for displaying lap times.
     *
     * Initializes the adapter, sets a vertical LinearLayoutManager, and enables
     * fixed size optimizations for smooth scrolling and efficient updates.
     */
    private fun setUpLapRecyclerView() {
        stopWatchLapAdapter = StopWatchLapAdapter()
        binding.stopwatchLapRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = stopWatchLapAdapter
        }
    }


    /**
     * Observes the stopwatch UI state and updates views accordingly.
     *
     * Uses a lifecycle-aware collector scoped to [viewLifecycleOwner] to prevent
     * updates when the fragment is not visible.
     */
    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stopWatchViewModel.uiState.collectLatest { state ->
                    updateUi(state)
                }
            }
        }
    }


    /**
     * Observes one-off UI effects such as toasts, permission requests, or service commands.
     *
     * Handles effects from [StopWatchViewModel] using lifecycle-aware collection.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            stopWatchViewModel.uiEffect.collect { effect ->
                when (effect) {
                    is StopwatchEffect.BlinkVisibilityChanged -> binding.stopwatchTimeTextGroup.isVisible =
                        effect.isVisible

                    is StopwatchEffect.ShowError -> requireContext().showToast(effect.message)
                    is StopwatchEffect.StartForegroundService -> startStopwatchService()
                    is StopwatchEffect.StopForegroundService -> stopStopwatchService()
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // UI Update Methods
    // ---------------------------------------------------------------------

    /**
     * Maps [StopwatchUiModel] state to the fragment's views.
     *
     * Pure UI update method: does not perform business logic.
     *
     * @param uiModel Current state of the stopwatch UI.
     */
    private fun updateUi(uiModel: StopwatchUiModel) = with(binding) {
        val toggleIcon = if (uiModel.isRunning) R.drawable.ic_pause else R.drawable.ic_play

        // Update stopwatch time and progress
        stopwatchSecondsText.text = uiModel.secondsText
        stopwatchMilliSecondsText.text = uiModel.milliSecondsText
        stopwatchProgressBarIndicator.progress = uiModel.progress

        // Update button icons and visibility
        toggleStopwatchBtn.setImageResource(toggleIcon)
        recordLapStopwatchBtn.isVisible = uiModel.isRunning
        resetStopwatchBtn.isVisible = uiModel.isRunning

        // Update lap list
        updateRecyclerView(uiModel.laps)
    }


    /**
     * Synchronizes the lap list with current stopwatch state.
     *
     * Animates layout changes and scrolls to the latest lap when added.
     *
     * @param lapsTimesList List of lap times to display.
     */
    private fun updateRecyclerView(lapsTimesList: List<StopwatchLapUiModel>) = with(binding) {
        val hasLaps = lapsTimesList.isNotEmpty()
        val newLapAdded = lapsTimesList.size > previousLapsCount

        // Toggle lap section visibility and animate layout
        if (stopwatchLapRv.isVisible != hasLaps) {
            stopwatchLapRv.isVisible = hasLaps
            stopWatchAnimator?.let { animator ->
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animator.animateStopWatchLayoutPortrait(isLapTimeAvailable = hasLaps)
                } else {
                    animator.animateStopWatchLayoutLandscape(isLapTimeAvailable = hasLaps)
                }
            }
        }

        // Update lap adapter and scroll to latest lap if needed
        if (hasLaps) {
            previousLapsCount = lapsTimesList.size
            stopWatchLapAdapter.submitList(lapsTimesList)
            if (newLapAdded) stopwatchLapRv.post {
                stopwatchLapRv.smoothScrollToPosition(lapsTimesList.size - 1)
            }
        }
    }


    // ---------------------------------------------------------------------
    // Effect Handler Methods
    // ---------------------------------------------------------------------


    private fun startStopwatchService() {
        val intent = createStopwatchServiceIntent(StopWatchBroadCastAction.START_FOREGROUND)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun stopStopwatchService() {
        val intent = createStopwatchServiceIntent(StopWatchBroadCastAction.STOP_FOREGROUND)
        requireContext().startService(intent)
    }

    private fun createStopwatchServiceIntent(action: String): Intent {
        return Intent(context, StopwatchService::class.java).apply {
            this.action = action
        }
    }

}