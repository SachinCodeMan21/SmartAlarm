package com.example.smartalarm.feature.alarm.presentation.view.activity

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.core.utility.extension.isSdk33AndAbove
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.databinding.ActivityAlarmBinding
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.services.AlarmService.Companion.ACTION_FINISH_ALARM_ACTIVITY
import com.example.smartalarm.feature.alarm.presentation.effect.mission.AlarmMissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.mission.ShowAlarmFragmentArgs
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import com.example.smartalarm.feature.alarm.utility.getParcelableExtraCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Activity responsible for displaying an active alarm and guiding the user through
 * a sequence of dismissal missions (e.g. Memory, Maths, Typing).
 *
 * This activity is launched when an alarm rings and is designed to:
 * - Appear over the lock screen
 * - Dismiss the keyguard
 * - Show a full-screen mission flow
 * - Handle mission progression, timeouts, and final alarm dismissal
 *
 * It uses a single-activity architecture with Jetpack Navigation Component.
 * The mission flow is driven entirely by [MyAlarmViewModel] via shared events and one-time effects.
 *
 * Key responsibilities:
 * - Coordinate between UI (fragments), ViewModel, and background alarm service
 * - Observe and react to [AlarmMissionEffect] emissions
 * - Manage progress bar visibility and timer updates
 * - Handle edge cases like service destruction or process death
 *
 * @see MyAlarmViewModel
 * @see com.example.smartalarm.feature.alarm.presentation.view.fragment.mission.ShowAlarmFragment
 * @see com.example.smartalarm.feature.alarm.presentation.job.MissionCountDownJobManager
 */
@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    companion object {

        // Tag used for logging within AlarmActivity for debugging purposes
        private const val TAG = "AlarmActivity"

        // Error message to log if the binding reference is null in AlarmActivity
        private const val BINDING_IS_NULL = "$TAG $BINDING_NULL"

        // Error message displayed when the Alarm ID is not found in the Intent of AlarmActivity
        private const val ERROR_ALARM_ID_NOT_FOUND = "Alarm ID Not Found In AlarmActivity Intent!"

        // Key used for passing alarm mission data to the AlarmActivity Mission Fragments
        const val ARGS_ALARM_MISSION_KEY = "$PACKAGE.ARG_ALARM_MISSION"

        // Key used for passing preview mission data to the AlarmActivity
        const val PREVIEW_MISSION_KEY = "$PACKAGE.PREVIEW_MISSION_KEY"

    }

    private var _binding: ActivityAlarmBinding? = null
    private val binding get() = _binding?: error(BINDING_IS_NULL)
    private val viewModel: MyAlarmViewModel by viewModels()
    private lateinit var navController : NavController
    private lateinit var alarmFinishReceiver: BroadcastReceiver




    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the activity by setting up essential components for UI, navigation, and system interactions.
     *
     * - **Edge-to-edge layout**: Ensures the content fits the screen, even with notches or curved edges.
     * - **View Binding**: Provides type-safe access to views and simplifies layout inflation.
     * - **System Bar Insets**: Adjusts padding to prevent content overlap with system UI elements like the status and navigation bars.
     * - **NavController**: Initializes fragment navigation, enabling smooth transitions.
     * - **Observers**: Sets up real-time updates for mission progress and UI effects.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        _binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        turnScreenOnAndKeyguardOff()
        setupNavigationAndProgressBarVisibility()
        setUpAlarmFinishReceiver()
        initializeNavGraphWithArguments()
        setUpMissionProgressObserver()
        setUpUIEffectObserver()

    }




    /**
     * Called when the activity is about to be destroyed.
     *
     * - Clears the view binding reference to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(alarmFinishReceiver)
        _binding = null
    }


    // ---------------------------------------------------------------------
    // UI SetUp Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the NavController and sets up destination change listener to control UI behavior.
     *
     * - **NavController Setup**: Initializes the NavController using the `NavHostFragment` for fragment navigation.
     * - **Progress Bar Visibility**: Listens for destination changes and toggles the visibility of the mission progress bar.
     *   - Hides the progress bar when navigating to `showAlarmFragment`.
     *   - Shows the progress bar for all other destinations.
     */
    private fun setupNavigationAndProgressBarVisibility() {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.missionFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.missionProgressBar.visibility = when (destination.id) {
                R.id.showAlarmFragment -> View.GONE
                else -> View.VISIBLE
            }
        }
    }


    /**
     * - Sets up the navigation graph with the necessary arguments, ensuring that the
     * correct alarm and preview data is passed to the navigation graph.
     * - This is essential for handling the proper flow of the alarm preview or regular alarm screen,
     * depending on the presence of a preview mission.
     * - The method initializes the view model with the preview state and configures the nav controller
     * with the appropriate arguments to reflect the correct screen behavior.
     */
    private fun initializeNavGraphWithArguments() {

        // Get the alarm ID from the extras, default to 0 if not found
        val alarmId = intent.extras?.getInt(AlarmKeys.ALARM_ID, 0) ?: error(ERROR_ALARM_ID_NOT_FOUND)

        // Retrieve the preview mission object if it exists
        val previewMission = intent.getParcelableExtraCompat<AlarmModel>(PREVIEW_MISSION_KEY)

        // Determine if this is a preview mode based on the presence of the preview mission
        val isPreview = previewMission != null

        // Update the view model with the preview state
        viewModel.setPreview(isPreview)

        // Prepare the arguments for the starting fragment
        val startArgs = ShowAlarmFragmentArgs(
            alarmId = if (isPreview) previewMission.id else alarmId,
            isPreview = isPreview,
            previewMission = previewMission
        )

        // Inflate the nav graph and set it with the provided start arguments
        val navGraph = navController.navInflater.inflate(R.navigation.mission_nav_graph)
        navController.setGraph(navGraph, startArgs.toBundle())
    }


    /**
     * Observes the timer progress from the view model and updates the progress bar.
     * This is active only when the fragment is in the STARTED state to avoid unnecessary updates.
     */
    private fun setUpMissionProgressObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.timerProgressState.collectLatest { progress ->
                    binding.missionProgressBar.progress = progress
                }
            }
        }
    }

    /**
     * 1. Observes one-time UI effects emitted from the [MyAlarmViewModel.uiEffect] flow.
     * 2. Launches a coroutine that starts collecting when the lifecycle is in STARTED state.
     * 3. Handles each effect accordingly:
     *    - [StartMissionFlow]: Begins the mission sequence for the given alarm ID.
     *    - [ShowAlarmMission]: Navigates to the specific mission screen with the given data.
     *    - [MissionTimeout]: Navigates the user back to the show alarm screen on timeout.
     *    - [MissionCompleted]: Triggers actions required after mission completion.
     *    - [FinishActivity]: Closes the current activity.
     */
    private fun setUpUIEffectObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiEffect.collectLatest { newEffect ->
                    when(newEffect){
                        is AlarmMissionEffect.StartMissionFlow -> startMissionFlow(newEffect.alarm)
                        is AlarmMissionEffect.ShowAlarmMission -> navigateToMissionFragment(newEffect.mission)
                        is AlarmMissionEffect.MissionTimeout -> handleMissionTimeout()
                        is AlarmMissionEffect.MissionCompleted -> handleMissionCompletion()
                        is AlarmMissionEffect.FinishActivity -> finish()
                        is AlarmMissionEffect.ShowToastMessage -> showToast(newEffect.toastMessage)
                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // Handle Alarm Mission Navigation
    // ---------------------------------------------------------------------

    /**
     * Initiates the mission flow for the specified alarm model.
     *
     * This method triggers the start of the mission sequence by emitting an event to the shared ViewModel.
     * The event signals the system to begin processing the mission flow associated with the provided alarm.
     *
     * Responsibilities:
     * 1. Emits a [AlarmMissionEvent.StartMissionFlow] event to the shared ViewModel.
     * 2. This event triggers the system to transition into the mission screen sequence.
     *
     * @param alarmModel The [AlarmModel] object containing details of the alarm for which the mission flow should be initiated.
     */
    private fun startMissionFlow(alarmModel: AlarmModel) {
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(alarmModel))
    }


    /**
     * Navigates to the appropriate mission fragment based on the given mission type.
     *
     * Responsibilities:
     * 1. Retrieves the navigation destination ID using the mission type.
     *    - Each mission type maps to a unique navigation action.
     * 2. Prepares a bundle with the mission object to pass as an argument.
     * 3. Triggers navigation to the selected mission fragment using the NavController.
     *
     * @param mission The [Mission] to be displayed in the corresponding fragment.
     */
    private fun navigateToMissionFragment(mission: Mission) {
        val destination = mission.type.getNavigationActionId()
        val bundle = Bundle().apply {
            putParcelable(ARGS_ALARM_MISSION_KEY, mission)
        }
        navController.popBackStack(R.id.showAlarmFragment, false)
        navController.navigate(destination, bundle)
    }

    /**
     * 1. Handles the scenario when a mission times out or is not completed in time.
     * 2. Navigates the user back to the ShowAlarmFragment to resume the alarm interaction.
     * 3. This ensures the alarm remains active until all required missions are successfully completed.
     */
    private fun handleMissionTimeout(){
        navController.popBackStack(R.id.showAlarmFragment,false)
    }


    /**
     * Handles the completion of the current mission.
     *
     * 1. Emits a [AlarmMissionEvent.MissionCompleted] event to the ViewModel.
     * 2. The ViewModel uses this event to determine whether to launch the next mission
     *    or stop the alarm if all missions are completed.
     */
    private fun handleMissionCompletion(){
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setUpAlarmFinishReceiver() {

        // Initialize the broadcast receiver
        alarmFinishReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == ACTION_FINISH_ALARM_ACTIVITY){
                    // Finish the activity when receiving the broadcast
                    Log.d("TAG","AlarmActivity onReceive Service Destroyed")
                    finish()
                }
            }
        }


        // Register the receiver with the action to listen for the broadcast
        val filter = IntentFilter(ACTION_FINISH_ALARM_ACTIVITY)

        // Register the receiver based on SDK version
        if (isSdk33AndAbove) {
            // For Android 12 (API level 31) and above, use RECEIVER_NOT_EXPORTED flag.
            registerReceiver(alarmFinishReceiver, filter,RECEIVER_NOT_EXPORTED)
        } else {
            // For lower versions, just register normally (no exported flag needed).
            registerReceiver(alarmFinishReceiver, filter)
        }
    }


    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // This wakes up the device if the screen is off
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }


}