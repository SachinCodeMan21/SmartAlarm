package com.example.smartalarm.feature.stopwatch.presentation.view

import android.content.Context
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
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.permission.MyPermissionChecker
import com.example.smartalarm.core.framework.permission.PermissionFlowDelegate
import com.example.smartalarm.core.framework.permission.model.AppFeature
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.MyAppPermissionRequester
import com.example.smartalarm.core.framework.permission.model.RequesterType
import com.example.smartalarm.core.framework.permission.model.Requirement
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.showSnackBar
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.databinding.FragmentStopwatchBinding
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.presentation.adapter.StopWatchLapAdapter
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchAnalyticsEvent
import com.example.smartalarm.feature.stopwatch.presentation.viewmodel.StopWatchViewModel
import com.example.smartalarm.feature.stopwatch.utility.StopwatchTimeFormatter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Primary View controller for the Stopwatch feature.
 *
 * This fragment implements a **Passive View** pattern, delegating all business
 * logic to [StopWatchViewModel] while handling visual state rendering and
 * lifecycle-specific hardware interactions (e.g., Foreground Services).
 *
 * ### Responsibilities:
 * - **State Observation**: Synchronizes UI components with a reactive state stream.
 * - **Event Dispatching**: Maps user interactions to a Unidirectional Data Flow (UDF).
 * - **Lifecycle Management**: Orchestrates the transition between UI execution and
 * background service persistence via [StopwatchEvent.MoveToBackground].
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


    /** * Orchestrates dynamic layout transitions (Portrait/Landscape) based on
     * the presence of lap data.
     */
    private var stopWatchAnimator: StopwatchLayoutAnimator? = null

    /** Adapter for displaying stopwatch lap times in a RecyclerView  */
    private lateinit var stopWatchLapAdapter: StopWatchLapAdapter


    /** Stores the previous number of recorded laps to manage animations and scrolling. */
    private var previousLapsCount = 0

    /** * Injected utility for locale-aware duration formatting.
     * Ensures consistent time representation across diverse regions.
     */
    @Inject
    lateinit var stopwatchTimeFormatter: StopwatchTimeFormatter

    @Inject
    lateinit var numberFormatter : NumberFormatter



    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
    @Inject
    lateinit var permissionChecker: MyPermissionChecker
    private lateinit var permissionRequester: MyAppPermissionRequester
    private lateinit var permissionFlowDelegate: PermissionFlowDelegate


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes hardware-level delegates (Permissions, Callbacks) that
     * require early attachment to the Fragment host.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This is the safest place to initialize it
        permissionRequester = MyAppPermissionRequester(
            caller = this,
            lifecycle = lifecycle, // Use Fragment lifecycle, not viewLifecycle
            checker = permissionChecker,
            type = RequesterType.BOTH
        )

        permissionFlowDelegate = PermissionFlowDelegate(
            fragment = this,
            checker = permissionChecker,
            requester = permissionRequester
        )
    }


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
     * Finalizes the UI initialization sequence.
     * Triggers the setup of observers and adapters only after the view hierarchy
     * is fully established and stable.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeProgressBarAnimator()
        setUpButtonClickListeners()
        setUpLapRecyclerView()
        setUpUIStateObserver()
        setUpUIEffectObserver()
    }

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

            if (stopWatchViewModel.getCurrentStopwatch().isRunning) {
                stopWatchViewModel.handleEvent(StopwatchEvent.ToggleRunState)
                return@setOnClickListener
            }

            requestNotificationPermission()
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
        stopWatchLapAdapter = StopWatchLapAdapter(numberFormatter, stopwatchTimeFormatter)
        binding.stopwatchLapRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = stopWatchLapAdapter
        }
    }


    /**
     * Collects and projects the [StopwatchUiModel] onto the view hierarchy.
     * Uses [repeatOnLifecycle] to ensure collection only occurs when the
     * view is in a valid state, preventing resource leakage.
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
     * Intercepts one-time [StopwatchEffect] signals.
     * Manages transient UI events such as SnackBar notifications and Service triggers
     * that do not persist within the primary UI state.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            stopWatchViewModel.uiEffect.collect { effect ->
                when (effect) {
                    is StopwatchEffect.BlinkVisibilityChanged -> binding.stopwatchTimeTextGroup.isVisible =
                        effect.isVisible

                    is StopwatchEffect.ShowError -> {
                       // val message = effect.error.asUiText().asString(requireContext())
                        binding.root.showSnackBar(effect.errorMessage, Snackbar.LENGTH_SHORT)
                    }

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
     * Synchronizes the layout with the current [StopwatchUiModel].
     * Performs atomic updates to time displays, progress indicators, and
     * interactive controls to maintain a consistent visual truth.
     */
    private fun updateUi(uiModel: StopwatchUiModel) = with(binding) {

        val toggleIcon = if (uiModel.isRunning) R.drawable.ic_pause else R.drawable.ic_play

        // Update stopwatch time and progress
        stopwatchSecondsText.text = stopwatchTimeFormatter.formatMainDisplay(uiModel.elapsedMillis,false)
        stopwatchMilliSecondsText.text = stopwatchTimeFormatter.formatFractionalSeconds(uiModel.elapsedMillis)
        stopwatchProgressBarIndicator.progress = uiModel.progress

        // Update button icons and visibility
        toggleStopwatchBtn.setImageResource(toggleIcon)
        toggleStopwatchBtn.contentDescription = getString(
            when {
                uiModel.isRunning -> R.string.pause_stopwatch
                uiModel.elapsedMillis == 0L -> R.string.start_stopwatch
                else -> R.string.resume_stopwatch
            }
        )

        recordLapStopwatchBtn.isVisible = uiModel.isRunning
        resetStopwatchBtn.isVisible = uiModel.isRunning

        // Update lap list
        updateRecyclerView(uiModel.laps)
    }


    /**
     * Manages the Lap List state and associated transition animations.
     * Implements orientation-aware layout adjustments and handles
     * auto-scrolling logic for improved user ergonomics.
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
     * Promotes the stopwatch session to a Foreground Service.
     * Ensures session durability and persistence.
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


    // ---------------------------------------------------------------------
    // Permission Methods
    // ---------------------------------------------------------------------
    /**
     * Negotiates Notification permissions required for Foreground Execution.
     * Implements a custom [PermissionFlowDelegate] to handle rationales and
     * permanent denials gracefully.
     */
    private fun requestNotificationPermission() {
        val requirement = Requirement(
            permission = MyAppPermission.Runtime.PostNotifications,
            rationaleTitle = getString(R.string.stopwatch_notification_permission_rationale_title),
            rationaleMessage = getString(R.string.stopwatch_notification_permission_rationale_message),
            permanentlyDeniedTitle = getString(R.string.stopwatch_notification_permission_permanently_denied_title),
            permanentlyDeniedMessage = getString(R.string.stopwatch_notification_permission_permanently_denied_message),
            toastOnDeny = getString(R.string.stopwatch_notification_permission_denied_toast),
            feature = AppFeature.STOPWATCH
        )

        permissionFlowDelegate.run(requirements = listOf(requirement)) {
            stopWatchViewModel.handleEvent(StopwatchEvent.ToggleRunState)
        }
    }


}