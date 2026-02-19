package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getColorCompat
import com.example.smartalarm.databinding.FragmentMemoryMissionBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.MemoryMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.MemoryMissionUIModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity.Companion.ARGS_ALARM_MISSION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MemoryMissionViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue
import com.example.smartalarm.core.utility.Constants.PASSED_MISSION_ARGS_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat

/**
 * A Fragment that manages the UI and logic for the Memory Mission game.
 * This fragment is responsible for displaying a grid of squares and handling the game logic, such as:
 * - Starting the memory mission and managing the game state.
 * - Displaying and updating the grid layout with squares that players interact with.
 * - Observing and responding to UI state and effects from the ViewModel.
 * - Handling mission completion and transitioning to other parts of the app if needed.
 *
 * The fragment uses ViewBinding for efficient view management and lifecycle-aware
 * UI state handling via LiveData and Flow.
 *
 * @property sharedViewModel The shared view model that handles cross-activity communication.
 * @property viewModel The view model for the Memory Mission game logic.
 * @property squareViews List of views representing the squares in the grid.
 */
@AndroidEntryPoint
class MemoryMissionFragment : Fragment() {

    companion object {

        // Tag used for logging within MemoryMissionFragment for debugging purposes
        private const val TAG = "MemoryMissionFragment"

        // Error message used when the view binding reference is null
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // Error message indicating that the passed mission arguments are null
        private const val MISSION_ARGS_IS_NULL = "$TAG $PASSED_MISSION_ARGS_NULL"

    }

    private var _binding: FragmentMemoryMissionBinding? = null
    private val binding get() = _binding?: error(BINDING_NULL_ERROR)
    private val sharedViewModel : MyAlarmViewModel by activityViewModels()
    private val viewModel: MemoryMissionViewModel by viewModels()
    private val squareViews = mutableListOf<View>()


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------


    /**
     * Called when the activity is created.
     * - Initializes the mission if it's the first creation (no saved instance).
     * - Retrieves the mission from arguments and triggers the initialization event in the view model.
     * - Throws an exception if the mission data is missing.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState==null){
            val mission = requireArguments().getParcelableCompat<Mission>(ARGS_ALARM_MISSION_KEY) ?: throw IllegalArgumentException(MISSION_ARGS_IS_NULL)
            viewModel.handleEvent(MemoryMissionEvent.InitializeMission(mission))
        }
    }


    /**
     * Inflates the fragment's view using view binding.
     *
     * 1. Initializes [_binding] with the layout inflater.
     * 2. Returns the root view of the layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoryMissionBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Called when the view is created. Sets up the UI and observers, and starts the memory mission if it's the first creation.
     * - Initializes the grid UI.
     * - Sets up observers for UI state and effects.
     * - Starts the memory mission if there is no saved instance state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGrid()
        setUpUIStateObserver()
        setUpUIEffectObserver()
        if (savedInstanceState == null){
            viewModel.handleEvent(MemoryMissionEvent.StartMission)
        }
    }


    /**
     * Cleans up resources when the view is destroyed.
     *
     *  Sets [_binding] to null to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // ---------------------------------------------------------------------
    // Initialization Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the grid layout by creating and adding square views to the grid.
     * - Removes any existing views from the grid.
     * - Calculates the square size based on grid size and spacing.
     * - Creates square views, sets their layout parameters, and adds them to the grid.
     * - Each square is clickable and triggers the "SquareSelected" event when tapped.
     */
    private fun setupGrid() {

        binding.gridSquares.removeAllViews()
        squareViews.clear()

        val gridSize = viewModel.gridSize
        val spacingPx = resources.getDimensionPixelSize(R.dimen.spacing_s)

        //val squareSize = calculateSquareSize(gridSize, spacingPx)

        with(binding.gridSquares) {
            columnCount = gridSize
            rowCount = gridSize
            useDefaultMargins = false
        }

        repeat(viewModel.totalSquares) { index ->
            val row = index / gridSize
            val col = index % gridSize

            val square = View(requireContext()).apply {
                id = View.generateViewId()
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)
                    setMargins(spacingPx / 2, spacingPx / 2, spacingPx / 2, spacingPx / 2)
                }

                setBackgroundColor(requireContext().getColorCompat(R.color.neutral))

                setOnClickListener {
                    viewModel.handleEvent(MemoryMissionEvent.SquareSelected(index))
                }
            }

            squareViews += square

            binding.gridSquares.addView(square)
        }
    }

    /**
     * Observes changes to the UI state from the [viewModel] and updates the UI accordingly.
     *
     * This observer is lifecycle-aware and starts collecting state only when the view's
     * lifecycle is at least in the [Lifecycle.State.STARTED] state.
     */
    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { updateUI(it) }
            }
        }
    }

    /**
     * Sets up an observer for UI effects and handles them when the lifecycle is in the STARTED state.
     * - Collects the latest UI effects from the view model.
     * - Triggers the "MissionCompleted" event in the shared view model when a mission is completed.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiEffect.collectLatest { newEffect ->
                    when(newEffect){
                        is MissionEffect.MissionCompleted -> sharedViewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
                    }
                }
            }
        }
    }




    // ---------------------------------------------------------------------
    // UI Update Methods
    // ---------------------------------------------------------------------
    /**
     * Updates the UI based on the latest [MemoryMissionUIModel]:
     *
     * 1. Updates round text and timer progress bar.
     * 2. Displays instruction text and its color.
     * 3. Handles visibility and content of the countdown.
     * 4. Updates background color and enable/disable state of each square view.
     * 5. Triggers the mission completion callback if the game is finished.
     */
    private fun updateUI(state: MemoryMissionUIModel) {

        with(binding) {

            tvRound.text = getString(
                R.string.round_text,
                sharedViewModel.getLocalizedNumber(state.currentRound,false),
                sharedViewModel.getLocalizedNumber(state.totalRounds,false)
            )

            tvInstructions.apply {
                text = state.instruction
                setTextColor(requireContext().getColorCompat(state.instructionColor))
            }

            tvCountdown.apply {
                text = state.countdownText ?: ""
                visibility = if (state.countdownText != null) View.VISIBLE else View.GONE
            }

            squareViews.forEachIndexed { index, view ->
                view.setBackgroundColor(requireContext().getColorCompat(state.squareColors.getOrElse(index){R.color.neutral}))
                view.isEnabled = state.isSquaresEnabled
            }

            if (state.timerProgress == 0){
                sharedViewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
            }

        }

    }

}
