package com.example.smartalarm.feature.timer.presentation.view.fragment

import android.content.Context
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
import com.example.smartalarm.core.utility.exception.asUiText
import com.example.smartalarm.core.framework.permission.MyPermissionChecker
import com.example.smartalarm.core.framework.permission.PermissionRationaleDialog
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.MyAppPermissionRequester
import com.example.smartalarm.core.framework.permission.model.MyPermissionStatus
import com.example.smartalarm.core.framework.permission.model.PermissionResult
import com.example.smartalarm.core.framework.permission.model.RequesterType
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.showSnackBar
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.databinding.FragmentTimerBinding
import com.example.smartalarm.feature.timer.presentation.effect.TimerEffect
import com.example.smartalarm.feature.timer.presentation.event.TimerEvent
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import com.example.smartalarm.feature.timer.presentation.viewmodel.TimerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
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

    @Inject
    lateinit var permissionChecker: MyPermissionChecker

    private lateinit var permissionRequester : MyAppPermissionRequester



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This is the safest place to initialize it
        permissionRequester = MyAppPermissionRequester(
            caller = this,
            lifecycle = lifecycle, // Use Fragment lifecycle, not viewLifecycle
            checker = permissionChecker,
            type = RequesterType.BOTH
        )
    }


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

            if (!timerViewModel.getIsTimerRunning()){
                checkAndStartTimer()
            }else{
                timerViewModel.handleEvent(TimerEvent.HandleStartTimerClick)
            }
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
     * - Displays a SnackBar with a message when [TimerEffect.ShowError] is received.
     */
    private fun setUpUiEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timerViewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is TimerEffect.NavigateToShowTimerScreen -> startActivity(Intent(requireContext(), ShowTimerActivity::class.java))
                        is TimerEffect.ShowError -> {
                            val message = effect.error.asUiText().asString(requireContext())
                            binding.root.showSnackBar(message, Snackbar.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }



    private fun checkAndStartTimer() {
        checkNotificationPermission()
    }




    // Permission Flow

    // Step 1: Runtime — Post Notifications
    private fun checkNotificationPermission() {
        val status = permissionChecker.checkRuntimeStatus(
            requireActivity(),
            MyAppPermission.Runtime.PostNotifications
        )
        when (status) {
            is MyPermissionStatus.RuntimeStatus.Granted      -> checkExactAlarmPermission()
            is MyPermissionStatus.RuntimeStatus.ShowRationale -> showNotificationRationale()
            is MyPermissionStatus.RuntimeStatus.Denied        -> requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        permissionRequester.requestRuntimePermission(MyAppPermission.Runtime.PostNotifications) { result ->
            when (result) {
                is PermissionResult.RuntimePermissionResult.Granted          -> checkExactAlarmPermission()
                is PermissionResult.RuntimePermissionResult.Denied           -> showNotificationRationale()
                is PermissionResult.RuntimePermissionResult.PermanentlyDenied -> showNotificationDeniedDialog()
            }
        }
    }

    private fun showNotificationRationale() {
        PermissionRationaleDialog.showRationale(
            fragmentManager = childFragmentManager,
            title = "Notification Permission Required",
            message = "The timer needs to show a notification so it keeps running reliably in the background and you can control it from your lock screen.",
            positiveText = "Grant Permission",
            negativeText = "Cancel",
            onPositive = { requestNotificationPermission() },
            onNegative = { requireContext().showToast("Timer requires notification permission") }
        )
    }

    private fun showNotificationDeniedDialog() {
        PermissionRationaleDialog.showGoToSettings(
            fragmentManager = childFragmentManager,
            title = "Notification Permission Blocked",
            message = "Notifications are blocked. Please enable them in settings to use the timer.",
            onPositive = {
                permissionRequester.requestAppSettings(MyAppPermission.Runtime.PostNotifications) { granted ->
                    if (granted) checkExactAlarmPermission()
                    else{ requireContext().showToast("Timer requires notification permission") }
                }
            },
            onNegative = { requireContext().showToast("Timer requires notification permission") }
        )
    }




    // Step 2: Special — Schedule Exact Alarms
    private fun checkExactAlarmPermission() {
        val status = permissionChecker.checkSpecialStatus(MyAppPermission.Special.ScheduleExactAlarms)
        when (status) {
            is MyPermissionStatus.SpecialStatus.Granted -> checkFullScreenIntentPermission()
            is MyPermissionStatus.SpecialStatus.Denied  -> showExactAlarmRationale()
        }
    }

    private fun showExactAlarmRationale() {
        PermissionRationaleDialog.showRationale(
            fragmentManager = childFragmentManager,
            title = "Exact Alarm Permission Required",
            message = "The timer needs to schedule exact alarms so it fires precisely when your countdown ends.",
            positiveText = "Go to Settings",
            negativeText = "Cancel",
            onPositive = { requestExactAlarmPermission() },
            onNegative = { requireContext().showToast("Timer requires exact alarm permission") }
        )
    }

    private fun requestExactAlarmPermission() {
        permissionRequester.requestSpecialPermission(MyAppPermission.Special.ScheduleExactAlarms) { result ->
            when (result) {
                is PermissionResult.SpecialPermissionResult.Granted -> checkFullScreenIntentPermission()
                is PermissionResult.SpecialPermissionResult.Denied  -> requireContext().showToast("Timer requires exact alarm permission")
            }
        }
    }





    // Step 3: Special — Full Screen Intent
    private fun checkFullScreenIntentPermission() {
        val status = permissionChecker.checkSpecialStatus(MyAppPermission.Special.FullScreenIntent)
        when (status) {
            is MyPermissionStatus.SpecialStatus.Granted -> onAllPermissionsGranted()
            is MyPermissionStatus.SpecialStatus.Denied  -> showFullScreenIntentRationale()
        }
    }

    private fun showFullScreenIntentRationale() {
        PermissionRationaleDialog.showRationale(
            fragmentManager = childFragmentManager,
            title = "Full Screen Notification Required",
            message = "The timer needs to display a full screen alert when your countdown finishes, even if your phone is locked.",
            positiveText = "Go to Settings",
            negativeText = "Cancel",
            onPositive = { requestFullScreenIntentPermission() },
            onNegative = { requireContext().showToast("Timer requires full screen notification permission") }
        )
    }

    private fun requestFullScreenIntentPermission() {
        permissionRequester.requestSpecialPermission(MyAppPermission.Special.FullScreenIntent) { result ->
            when (result) {
                is PermissionResult.SpecialPermissionResult.Granted -> onAllPermissionsGranted()
                is PermissionResult.SpecialPermissionResult.Denied  -> requireContext().showToast("Timer requires full screen notification permission")
            }
        }
    }




    // All permissions cleared — start the timer
    private fun onAllPermissionsGranted() {
        timerViewModel.handleEvent(TimerEvent.HandleStartTimerClick)
    }


}