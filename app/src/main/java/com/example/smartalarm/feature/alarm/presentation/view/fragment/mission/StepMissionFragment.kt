package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission

import android.Manifest
import android.graphics.drawable.AnimationDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PASSED_MISSION_ARGS_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.core.utility.extension.isSdk29AndAbove
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.core.utility.extension.toLocalizedString
import com.example.smartalarm.core.framework.permission.PermissionManager
import com.example.smartalarm.databinding.FragmentStepMissionBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.StepMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.StepMissionUiModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity.Companion.ARGS_ALARM_MISSION_KEY
import com.example.smartalarm.feature.alarm.presentation.view.dialog.StepRecognitionDialogFragment
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.StepMissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue
import kotlin.math.sqrt

/**
 * A Fragment responsible for handling a step-based mission using device sensors.
 *
 * This component:
 * - Retrieves and initializes the mission passed through fragment arguments.
 * - Manages required permissions for step recognition (`ACTIVITY_RECOGNITION` on API 29+).
 * - Listens to step detector and accelerometer sensor events.
 * - Updates the mission UI based on step progress.
 * - Emits mission-completion events through a shared ViewModel.
 *
 * It uses:
 * - `Sensor.TYPE_STEP_DETECTOR` for detecting real step events.
 * - `Sensor.TYPE_ACCELEROMETER` for real-time acceleration magnitude monitoring.
 *
 * Lifecycle responsibilities:
 * - Sensors are registered when the view is created and unregistered when destroyed.
 * - View bindings are safely cleaned up to avoid memory leaks.
 *
 * Hilt (`@AndroidEntryPoint`) is used for dependency injection.
 *
 * Implements:
 * - [SensorEventListener] to receive sensor updates.
 */
@AndroidEntryPoint
class StepMissionFragment : Fragment(), SensorEventListener {

    companion object {

        // Used for logging purposes to identify log messages related to this fragment.
        private const val TAG = "StepMissionFragment"

        // Error message to log when the view binding is null.
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        //  Error message to log when the mission arguments passed to the fragment are null.
        private const val MISSION_ARGS_IS_NULL = "$TAG $PASSED_MISSION_ARGS_NULL"
    }

    private var _binding: FragmentStepMissionBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val sharedViewModel : MyAlarmViewModel by activityViewModels()
    private val viewModel: StepMissionViewModel by viewModels()
    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var accelerometer: Sensor? = null

    private lateinit var sensorPermissionLauncher : ActivityResultLauncher<String>

    @Inject
    lateinit var permissionManager: PermissionManager
    private var stepRecognitionDialog: StepRecognitionDialogFragment? = null



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called when the fragment is created.
     *
     * Initializes the mission by retrieving it from the fragment's arguments
     * and passes it to the ViewModel to handle the initialization event.
     *
     * @param savedInstanceState If the fragment is being reinitialized after
     *                           being previously destroyed, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            // Retrieve the mission object from the fragment arguments.
            val mission = requireArguments().getParcelableCompat<Mission>(ARGS_ALARM_MISSION_KEY)
                ?: throw IllegalArgumentException(MISSION_ARGS_IS_NULL)

            // Pass the mission to the ViewModel for initialization.
            viewModel.handleEvent(StepMissionEvent.InitializeMission(mission))
        }
    }


    /**
     * Called to inflate the view for the fragment.
     *
     * This method sets up the fragment's view binding using the provided inflater
     * and container. It returns the root view for the fragment's layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     *
     * This method is responsible for registering sensor listeners, checking for
     * required permissions, observing the ViewModel for updates, and setting up
     * sensors for the step counting functionality.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerSensorLauncher()
        checkPermissions()
        observeViewModel()
        setupSensors()
    }

    /**
     * Called when the fragment's view is about to be destroyed.
     *
     * This method is responsible for cleaning up resources related to the sensors
     * and nullifying the bindings to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the sensor event listener.
        sensorManager?.unregisterListener(this)

        // Nullify sensor and binding references to release resources.
        sensorManager = null
        stepSensor = null
        accelerometer = null
        _binding = null
    }



    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Registers an Activity Result Launcher to handle the `ACTIVITY_RECOGNITION`
     * permission request required for step detection.
     *
     * If the permission is granted, sensors are initialized.
     * If denied, a toast is shown informing the user that the permission is required.
     */
    private fun registerSensorLauncher() {
        sensorPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) setupSensors()
            else requireContext().showToast("Permission required for step detection")
        }
    }

    /**
     * Checks whether the required sensor permission has been granted.
     *
     * - On Android 10+ (API 29+), the `ACTIVITY_RECOGNITION` permission must be requested.
     * - If the permission is already granted or not required, sensors are initialized directly.
     */
    private fun checkPermissions() {
        if (requireContext().isSdk29AndAbove && !permissionManager.isSensorPermissionGranted()) {
            sensorPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            setupSensors()
        }
    }

    /**
     * Initializes the step detector and accelerometer sensors, and registers listeners for them.
     *
     * - If a sensor is available, its listener is registered with an appropriate delay.
     * - If unavailable, a toast informs the user that the sensor is missing.
     *
     * This method must be called only after permissions are granted.
     */
    private fun setupSensors() {
        sensorManager = ContextCompat.getSystemService(requireContext(), SensorManager::class.java)
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: requireContext().showToast("Step detector sensor not available")

        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?:  requireContext().showToast("Accelerometer sensor not available")

        showStepRecognitionDialog()


    }

    /**
     * Observes ViewModel state (`uiState`) and one-time effects (`uiEffect`)
     * while the fragment is in the STARTED state.
     *
     * - `uiState` updates trigger UI refresh via [updateUI]
     * - `uiEffect` handles mission completion events and dispatches them
     *   to the shared ViewModel.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect { updateUI(it) } }
                launch { viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        MissionEffect.MissionCompleted -> { sharedViewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted) }
                    }
                } }
            }
        }
    }


    /**
     * Updates all UI elements related to the step mission.
     *
     * - Displays the current step count with animation.
     * - Shows and updates the previous step view if the user has progressed.
     * - Shows and updates the next step view if more steps remain.
     *
     * @param state The current UI state containing step progress and total steps.
     */
    private fun updateUI(state: StepMissionUiModel) {

        binding.currentStepTv.text = state.stepCount.toLong().toLocalizedString()

        if (binding.currentStepTv.background !is AnimationDrawable) {
            binding.currentStepTv.setBackgroundResource(R.drawable.bg_shake_current_glow)
            (binding.currentStepTv.background as? AnimationDrawable)?.start()
        }

        binding.postStepTv.apply {
            visibility = if (state.stepCount > 0) View.VISIBLE else View.INVISIBLE
            if (state.stepCount > 0) {
                text = (state.stepCount - 1).toLong().toLocalizedString()
                setBackgroundResource(R.drawable.bg_shake_done)
            }
        }

        binding.nextStepTv.apply {
            val next = state.stepCount + 1
            visibility = if (next <= state.totalSteps) View.VISIBLE else View.INVISIBLE
            if (next <= state.totalSteps) {
                text = next.toLong().toLocalizedString()
                setBackgroundResource(R.drawable.bg_shake_pending)
            }
        }
    }




    // ---------------------------------------------------------------------
    // Sensor Overridden Methods
    // ---------------------------------------------------------------------

    /**
     * Called when a sensor value changes.
     *
     * This method handles updates from:
     * - **Step Detector Sensor**: Triggers a `StepDetected` event in the ViewModel
     *   whenever a step is detected by the sensor.
     * - **Accelerometer Sensor**: Calculates the magnitude of acceleration using
     *   the x, y, and z axis values, then dispatches an `AccelerationChanged` event
     *   with the computed magnitude.
     *
     * @param event The sensor event containing updated values from the respective sensor.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {

            Sensor.TYPE_STEP_DETECTOR -> {
                // Dismiss the dialog once the first step is detected
                stepRecognitionDialog?.dismiss()
                stepRecognitionDialog = null
                viewModel.handleEvent(StepMissionEvent.StepDetected)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val (x, y, z) = event.values
                val magnitude = sqrt(x * x + y * y + z * z)
                viewModel.handleEvent(StepMissionEvent.AccelerationChanged(magnitude))
            }
        }
    }

    /**
     * Called when the accuracy of a sensor changes.
     *
     * This implementation does nothing because accuracy changes are not relevant
     * for step detection or accelerometer magnitude calculations in this feature.
     *
     * @param sensor The sensor reporting the accuracy change.
     * @param accuracy The new accuracy level of the sensor.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    // ---------------------------------------------------------------------
    // Pre Sensor Dialog Methods
    // ---------------------------------------------------------------------
    /**
     * Displays the StepRecognitionDialogFragment if it's not already shown.
     *
     * This function checks if the `stepRecognitionDialog` is null. If it is, it initializes
     * a new instance of the StepRecognitionDialogFragment and shows it using the childFragmentManager.
     * This ensures that only one instance of the dialog is displayed at a time.
     */
    private fun showStepRecognitionDialog() {
        // Check if dialog is already showing
        if (stepRecognitionDialog == null) {
            stepRecognitionDialog = StepRecognitionDialogFragment.newInstance()
            stepRecognitionDialog?.show(childFragmentManager, "StepRecognitionDialog")
        }
    }

}