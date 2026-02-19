package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.smartalarm.core.utility.extension.toLocalizedString
import com.example.smartalarm.databinding.FragmentShakeMissionBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShakeMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShakeMissionUiModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity.Companion.ARGS_ALARM_MISSION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.ShakeMissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.getValue
import kotlin.math.sqrt

/**
 * - A fragment that manages the "shake mission" in the app.
 *
 * - This fragment listens for accelerometer sensor changes and reacts to shake events to progress through the mission.
 *
 * - It observes the state of the shake mission through the ViewModel and updates the UI based on
 * the current shake count, total shakes, and any mission completion events.
 *
 * - It also handles cleaning up resources when the fragment's lifecycle ends to prevent memory leaks.
 */
@AndroidEntryPoint
class ShakeMissionFragment : Fragment(), SensorEventListener  {


    companion object {
        private const val TAG = "ShakeMissionFragment"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_IS_NULL = "$TAG $PASSED_MISSION_ARGS_NULL"
    }

    private var _binding: FragmentShakeMissionBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val sharedViewModel : MyAlarmViewModel by activityViewModels()
    private val shakeViewModel: ShakeMissionViewModel by viewModels()
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called when the fragment is created. If it's the first time the fragment is created, it retrieves
     * the mission argument and initializes the mission by passing it to the ViewModel.
     *
     * @param savedInstanceState The saved state of the fragment if it exists.
     *                           If `null`, the fragment is being created for the first time.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val mission = requireArguments().getParcelableCompat<Mission>(ARGS_ALARM_MISSION_KEY)
                ?: throw IllegalArgumentException(MISSION_ARGS_IS_NULL)
            shakeViewModel.handleEvent(ShakeMissionEvent.InitializeMission(mission))

            Log.d("TAG","Shake Mission :- $mission")
        }
    }

    /**
     * Called to inflate the fragment's view and initialize the view binding.
     * This is where the UI components are prepared, and the root view is returned to be displayed.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShakeMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     *  Used for additional setup like registering sensors  and observing ViewModel events.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSensors()
        observeViewModel()
    }

    /**
     *  Registers the sensor listener to start receiving sensor updates when the fragment is visible.
     */
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * Unregisters the sensor listener to stop receiving sensor updates when the fragment is not visible.
     */
    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    /**
     * Called when the fragment's view is destroyed. This method cleans up the sensor manager, accelerometer,
     * and the view binding to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager?.unregisterListener(this)
        sensorManager = null
        accelerometer = null
        _binding = null
    }




    // ---------------------------------------------------------------------
    // Initialization Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the sensors by retrieving the SensorManager system service and initializing
     * the accelerometer sensor. This method ensures that the sensor manager is properly
     * initialized and ready to listen for accelerometer data.
     */
    private fun setupSensors() {
        // Get the SensorManager system service
        sensorManager = ContextCompat.getSystemService(requireContext(), SensorManager::class.java)

        // Get the default accelerometer sensor
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    /**
     * - Observes the ViewModel's state and effects.
     * - The method launches two separate coroutines within the `lifecycleScope` to collect the UI state and effects.
     * - The state is used to update the UI, while the effects trigger side effects such as handling mission completion.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Repeat the block when the lifecycle is in the STARTED state
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect and handle UI state updates
                launch {
                    shakeViewModel.uiState.collect { state -> updateUI(state) }
                }

                // Collect and handle UI effect updates
                launch {
                    shakeViewModel.uiEffect.collect { effect ->
                        when (effect) {
                            MissionEffect.MissionCompleted -> {
                                // Trigger a shared event when the mission is completed
                                sharedViewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
                            }
                        }
                    }
                }
            }
        }
    }



    // ---------------------------------------------------------------------
    //  Update UI Method
    // ---------------------------------------------------------------------
    /**
     * Updates the UI elements based on the current [ShakeMissionUiModel].
     *
     * - Updates the progress bar with the current timer progress.
     * - Displays the current shake count prominently in the center with an animated glow.
     * - Shows the previous shake count on the left if applicable.
     * - Shows the next shake count on the right if within total shakes.
     * - Sets appropriate backgrounds to indicate current, done, and pending shake states.
     *
     * @param state The current UI state of the shake mission.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUI(state: ShakeMissionUiModel) = with(binding){

        currentShakeTv.text = state.shakeCount.toLong().toLocalizedString()
        currentShakeTv.setBackgroundResource(R.drawable.bg_shake_current_glow)
        (currentShakeTv.background as? AnimationDrawable)?.start()

        postShakeTv.apply {
            visibility = if (state.shakeCount > 0) View.VISIBLE else View.INVISIBLE
            text = (state.shakeCount - 1).toLong().toLocalizedString()
            setBackgroundResource(R.drawable.bg_shake_done)
        }

        tvRight.apply {

            val nextCount = state.shakeCount + 1
            visibility = if (nextCount <= state.totalShakes) View.VISIBLE else View.INVISIBLE
            if (nextCount <= state.totalShakes) {
                text = nextCount.toLong().toLocalizedString()
                setBackgroundResource(R.drawable.bg_shake_pending)
            }
        }

    }

    // ---------------------------------------------------------------------
    //  Handle onSensorChanged & onAccuracyChanged Methods
    // ---------------------------------------------------------------------

    /**
     * - Called when there is a change in the sensor data.
     * - This method calculates the acceleration from the accelerometer's X, Y, and Z axis values, subtracts the Earth's gravity to get the
     * relative acceleration, and then passes the calculated acceleration value to the ViewModel.
     *
     * @param event The sensor event containing the sensor data (X, Y, Z values).
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return  // Return early if the event is null

        // Extract X, Y, Z values from the event
        val (x, y, z) = event.values

        // Calculate the total acceleration (excluding gravity)
        val acceleration = sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

        // Pass the acceleration value to the ViewModel to handle the shake event
        shakeViewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(acceleration))
    }

    /**
     * Called when the accuracy of the sensor changes. This method is a no-op in this case,
     * as we are not handling any changes in sensor accuracy.
     *
     * @param p0 The sensor whose accuracy has changed.
     * @param p1 The new accuracy level of the sensor.
     */
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}


}