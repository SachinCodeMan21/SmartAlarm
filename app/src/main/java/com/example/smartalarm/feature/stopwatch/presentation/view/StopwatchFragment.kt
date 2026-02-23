package com.example.smartalarm.feature.stopwatch.presentation.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.core.exception.asUiText
import com.example.smartalarm.core.permission.PermissionChecker
import com.example.smartalarm.core.permission.PermissionRequester
import com.example.smartalarm.core.permission.model.AppPermission
import com.example.smartalarm.core.permission.PermissionCoordinator
import com.example.smartalarm.core.permission.model.AppFeature
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.showSnackBar
import com.example.smartalarm.databinding.FragmentStopwatchBinding
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.presentation.adapter.StopWatchLapAdapter
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.presentation.viewmodel.StopWatchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


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

    /** Adapter for displaying stopwatch lap times in a RecyclerView  */
    private lateinit var stopWatchLapAdapter: StopWatchLapAdapter



    @Inject
    lateinit var checker: PermissionChecker

    private lateinit var permissionCoordinator: PermissionCoordinator

    /** Stores the previous number of recorded laps to manage animations and scrolling. */
    private var previousLapsCount = 0



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
        setUpPermissionCoordinator()
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
            if (stopWatchViewModel.getIsStopwatchRunning()){
                stopWatchViewModel.handleEvent(StopwatchEvent.ToggleRunState)
            }
            else{
                permissionCoordinator.runPermissionGatekeeper(listOf(AppPermission.Runtime.PostNotifications),requireActivity(), AppFeature.STOPWATCH){
                    stopWatchViewModel.handleEvent(StopwatchEvent.ToggleRunState)
                }
            }
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
                    is StopwatchEffect.BlinkVisibilityChanged -> binding.stopwatchTimeTextGroup.isVisible = effect.isVisible
                    is StopwatchEffect.ShowError -> {
                        val message = effect.error.asUiText().asString(requireContext())
                        binding.root.showSnackBar(message, Snackbar.LENGTH_SHORT)
                    }
                    is StopwatchEffect.StartForegroundService -> startStopwatchService()
                    is StopwatchEffect.StopForegroundService -> stopStopwatchService()
                }
            }
        }
    }


    /**
     * Initializes the PermissionCoordinator for handling runtime permissions in this fragment.
     *
     * This sets up a centralized mechanism to:
     *  - Check whether permissions are granted,
     *  - Request permissions when needed, and
     *  - Provide rationales to the user if the system indicates it is necessary.
     *
     * Using a coordinator ensures permission logic is consistent, decoupled from UI code,
     * and lifecycle-aware, preventing memory leaks and redundant permission requests.
     */
    private fun setUpPermissionCoordinator() {

        val requester = PermissionRequester(
            caller = this,
            lifecycleOwner = this,
            context = requireContext(),
            permissionChecker = checker,
            rationaleProvider = { permissionName ->
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permissionName
                )
            }
        )

        permissionCoordinator = PermissionCoordinator(
            context = requireContext(),
            requester = requester,
            checker = checker,
            fragmentManager = childFragmentManager,
            lifecycleOwner = viewLifecycleOwner
        )

    }





    // ---------------------------------------------------------------------
    // UI Update Methods
    // ---------------------------------------------------------------------

    /**
     * Synchronizes the stopwatch screen with the latest UI state.
     *
     * This keeps the visual representation (time, progress, controls, and laps)
     * fully aligned with the underlying stopwatch state, ensuring the UI
     * accurately reflects whether the stopwatch is running or paused
     * and maintains a consistent, predictable user experience.
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
     * Updates the lap list UI to reflect the current stopwatch state.
     *
     * This ensures the lap section is only visible when laps exist,
     * keeps the layout visually balanced through orientation-aware animations,
     * and automatically scrolls to the newest lap to maintain focus on
     * the most recent user action.
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

    /**
     * Starts the StopwatchService in foreground mode when the stopwatch begins running.
     *
     * This ensures the stopwatch continues running reliably in the background
     * and is not killed by the system while active.
     */
    private fun startStopwatchService() {
        val intent = createStopwatchServiceIntent(StopWatchBroadCastAction.START_FOREGROUND)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    /**
     * Stops the foreground state of StopwatchService when the stopwatch is no longer running.
     *
     * This prevents unnecessary background execution and releases system resources
     * once the stopwatch has been restarted or stopped.
     */
    private fun stopStopwatchService() {
        val intent = createStopwatchServiceIntent(StopWatchBroadCastAction.STOP_FOREGROUND)
        requireContext().startService(intent)
    }


    /**
     * Creates an intent used to communicate start/stop actions to StopwatchService.
     *
     * Centralizing intent creation avoids duplication and keeps service interaction consistent.
     */
    private fun createStopwatchServiceIntent(action: String): Intent {
        return Intent(context, StopwatchService::class.java).apply {
            this.action = action
        }
    }

}