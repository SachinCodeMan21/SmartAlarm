package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.exception.asUiText
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.databinding.FragmentShowAlarmBinding
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.effect.mission.ShowAlarmEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShowAlarmEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShowAlarmUiModel
import com.example.smartalarm.feature.alarm.presentation.view.handler.AlarmSwipeHandler
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.ShowAlarmViewModel
import com.example.smartalarm.feature.alarm.utility.getLocalizedDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment that displays a single alarm and handles user interactions
 * such as snoozing, stopping, or starting missions associated with the alarm.
 *
 * This fragment observes the [ShowAlarmViewModel] for UI state updates and
 * one-time UI effects. It updates the UI accordingly and delegates user
 * actions to the ViewModel via events.
 *
 * Key responsibilities:
 * 1. **Lifecycle Handling**: Loads alarm data when first created and cleans
 *    up view bindings to prevent memory leaks.
 * 2. **UI State Observation**: Observes `uiState` from the ViewModel to
 *    update alarm time, day, snooze count, and button states.
 * 3. **UI Effect Handling**: Observes `uiEffect` from the ViewModel for
 *    one-time effects such as starting mission flows or finishing the activity.
 * 4. **User Interaction**: Handles button clicks for snoozing, stopping the
 *    alarm, or starting missions, forwarding these events to the ViewModel.
 * 5. **Mission Flow Delegation**: Communicates with a shared [MyAlarmViewModel]
 *    to trigger mission-related workflows.
 *
 * This fragment uses [androidx.viewbinding.ViewBinding] for efficient and safe view access and
 * Hilt for dependency injection ([@AndroidEntryPoint]).
 */
@AndroidEntryPoint
class ShowAlarmFragment : Fragment() {

    companion object {

        // Tag used for logging within the ShowAlarmFragment for debugging purposes
        private const val TAG = "ShowAlarmFragment"

        // Error message when the binding reference in ShowAlarmFragment is null
        private const val BINDING_IS_NULL = "$TAG $BINDING_NULL"

    }

    private var _binding: FragmentShowAlarmBinding? = null
    private val binding get() = _binding?: error(BINDING_IS_NULL)
    private val sharedMissionViewModel : MyAlarmViewModel by activityViewModels()
    private val showAlarmViewmodel : ShowAlarmViewModel by viewModels()
    private val args : ShowAlarmFragmentArgs by navArgs()

    @Inject
    lateinit var vibrationManager: VibrationManager
    private lateinit var alarmSwipeHandler: AlarmSwipeHandler




    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * Inflates the layout for this fragment using ViewBinding.
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowAlarmBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     *
     * This method sets up:
     * - **Listeners** for UI interactions.
     * - **Observers** for UI state changes.
     * - **UI effect observers** to handle side effects like navigation or showing toasts.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState A Bundle containing the saved instance state, if available.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpUIStateObserver()
        setUpUIEffectObserver()
        setUpSwipeGesture()
    }

    /**
     * Called when the activity is started.
     * - This method checks if the app is in preview mode or not.
     * - If it's not a preview, it loads the alarm based on the provided args alarmId.
     * - If it's in preview mode, it loads the preview mission instead.
     */
    override fun onStart() {
        super.onStart()
        if (!args.isPreview) { showAlarmViewmodel.handleEvent(ShowAlarmEvent.LoadAlarm(args.alarmId)) }
        else{ args.previewMission?.let { showAlarmViewmodel.handleEvent(ShowAlarmEvent.LoadPreview(it)) } }
    }

    /**
     * Called when the view previously created by `onCreateView` has been detached.
     *
     * Cleans up the binding to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // ---------------------------------------------------------------------
    // UI SetUp Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up an observer for the ViewModel's UI state.
     *
     * Launches a coroutine in the fragment's lifecycle scope that collects
     * the latest `uiState` from the ViewModel whenever the fragment's
     * lifecycle is at least `STARTED`. Updates the UI accordingly by calling
     * [updateUI] with the new state.
     */
    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                showAlarmViewmodel.uiState.collectLatest { newState ->
                    updateUI(newState)
                }
            }
        }
    }

    /**
     * Sets up an observer to collect UI effects from the [ShowAlarmViewModel].
     * This method collects UI effects in the lifecycle scope, and based on the type of effect, it triggers corresponding actions:
     * - Starts the mission flow with the provided alarm model.
     * - Finishes the current activity.
     * - Shows a toast message with the provided text.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                showAlarmViewmodel.uiEffect.collectLatest { newEffect ->
                    when (newEffect) {
                        is ShowAlarmEffect.StartMissionFlow -> startMissionFlow(newEffect.alarmModel)
                        is ShowAlarmEffect.FinishActivity -> requireActivity().finish()
                        is ShowAlarmEffect.ShowToastMessage -> requireContext().showToast(newEffect.toastMessage)
                        is ShowAlarmEffect.ShowError -> {
                            val errorMessage  = newEffect.error.asUiText().asString(requireContext())
                            requireContext().showToast(errorMessage)
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets up the swipe gesture for the draggable thumb.
     * Allows user to drag left for snooze or right for stop/complete mission.
     */
    private fun setUpSwipeGesture() {
        alarmSwipeHandler = AlarmSwipeHandler(
            binding = binding,
            context = requireContext(),
            vibrationManager = vibrationManager,
            onSnooze = {
                showAlarmViewmodel.handleEvent(ShowAlarmEvent.SnoozeAlarm)
            },
            onStopOrStartMission = {
                showAlarmViewmodel.handleEvent(ShowAlarmEvent.StopAlarmOrStartMissions)
            },
            onShowToast = { message ->
                requireContext().showToast(message)
            }
        )
        lifecycle.addObserver(alarmSwipeHandler)
        alarmSwipeHandler.setupSwipeGesture()
    }

    /**
     * Sets up click listeners for the UI buttons.
     * Each button triggers a corresponding event in the [showAlarmViewmodel]:
     * - Snooze button triggers the "SnoozeAlarm" event.
     * - Stop button triggers the "StopAlarmOrStartMissions" event.
     * - Exit Preview button triggers the "ExitPreview" event.
     */
    private fun setUpListeners() {

        binding.exitPreviewBtn.setOnClickListener {
            showAlarmViewmodel.handleEvent(ShowAlarmEvent.ExitPreview)
        }

    }





    // ---------------------------------------------------------------------
    // Update UI State
    // ---------------------------------------------------------------------

    /**
     * Updates the UI with the latest state provided by [newState].
     * This method modifies various UI elements based on the current alarm state:
     * - Updates the alarm time and day.
     * - Enables or disables the snooze button and controls its visibility based on the snooze count.
     * - Updates the snooze count text.
     * - Changes the stop button text depending on whether a mission is available.
     * - Shows or hides the exit preview button based on the preview mode.
     *
     * @param newState The [ShowAlarmUiModel] containing the current state of the alarm.
     */
    private fun updateUI(newState: ShowAlarmUiModel) = with(binding) {
        alarmTime.text = newState.formattedAlarmTime
        alarmDay.text = getLocalizedDay(requireContext())
        labelTv.text = newState.alarmLabel
        snoozeCountText.text = getString(R.string.snooze_left,sharedMissionViewModel.getLocalizedNumber(newState.snoozeCount,false))
        exitPreviewBtn.isVisible = args.isPreview
        alarmSwipeHandler.updateState(
            snoozeCount = newState.snoozeCount,
            isMissionAvailable = newState.isMissionAvailable
        )
    }




    // ---------------------------------------------------------------------
    // UI Effect Handler Method
    // ---------------------------------------------------------------------

    /**
     * Starts the mission flow by triggering the "StartMissionFlow" event with the provided alarm.
     * This method sends the alarm to the shared view model to initiate the mission flow associated with the alarm.
     *
     * @param alarm The [AlarmModel] representing the current alarm to start the mission flow with.
     */
    private fun startMissionFlow(alarm: AlarmModel) {
        sharedMissionViewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(alarm))
    }


}