package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission

import android.os.Bundle
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
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PASSED_MISSION_ARGS_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.FragmentMathMissionBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.MathMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.MathMissionUiModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity.Companion.ARGS_ALARM_MISSION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MathMissionViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment that handles the math mission UI and user interactions for the mission.
 *
 * It interacts with the [MathMissionViewModel] to manage the mission's state and events.
 * Observes UI state and effects, updating the UI accordingly.
 * Initializes the mission based on arguments and listens for user inputs (e.g., submitting answers).
 *
 * @throws IllegalArgumentException If the mission data passed to the fragment is null.
 */
@AndroidEntryPoint
class MathMissionFragment : Fragment() {

    companion object{

        // TAG used for logging in the MathMissionFragment class.
        private const val TAG = "MathMissionFragment"

        // Error message when the data binding is null in the fragment.
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // Error message when mission arguments are not passed or are null.
        private const val MISSION_ARGS_IS_NULL = "$TAG $PASSED_MISSION_ARGS_NULL"

    }

    private var _binding: FragmentMathMissionBinding? = null
    private val binding get() = _binding?:error(BINDING_NULL_ERROR)
    private val sharedViewModel : MyAlarmViewModel by activityViewModels()
    private val viewModel: MathMissionViewModel by viewModels()


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMathMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setUpUIStateObserver()
        setUpUIEffectObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ---------------------------------------------------------------------
    //  Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the UI by setting up the mission and handling user interactions.
     * - Retrieves the mission data from arguments and starts the mission.
     * - Sets up a listener for the submit button to handle answer submission.
     * Throws an [IllegalArgumentException] if the mission data is null.
     */
    private fun setUpUI() {

        val mission = requireArguments().getParcelableCompat<Mission>(ARGS_ALARM_MISSION_KEY) ?: throw IllegalArgumentException(MISSION_ARGS_IS_NULL)

        binding.btnSubmit.setOnClickListener {
            val answerText = binding.etAnswer.text.toString()
            viewModel.handleEvent(MathMissionEvent.SubmitAnswer(answerText))
        }

        viewModel.handleEvent(MathMissionEvent.StartMission(mission))

    }


    /**
     * Observes changes to the UI state and updates the UI accordingly.
     * Collects from the [MathMissionViewModel.uiState] flow and invokes [updateUI] on each new state.
     */
    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    /**
     * Observes and handles UI effect events such as mission completion.
     * Collects from the [MathMissionViewModel.uiEffect] flow and performs corresponding actions on new events.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collectLatest { newState ->
                    when(newState) {
                        is MissionEffect.MissionCompleted -> sharedViewModel.handleSharedEvent(
                            AlarmMissionEvent.MissionCompleted)
                    }
                }
            }
        }
    }



    // ---------------------------------------------------------------------
    // UI Update Methods
    // ---------------------------------------------------------------------

    /**
     * Updates the UI elements based on the provided [MathMissionUiModel] state.
     */
    private fun updateUI(state: MathMissionUiModel) {
        binding.apply {
            tvRound.text = state.roundText
            tvQuestion.text = state.question
            tvInstructions.text = state.instruction
            tvInstructions.setTextColor(ContextCompat.getColor(requireContext(), state.instructionColor))
            btnSubmit.isEnabled = state.isSubmitEnabled
            etAnswer.isEnabled = state.isInputEnabled
            imageView7.setImageResource(state.statusImageRes)
            if (state.clearInput) etAnswer.text?.clear()
        }
    }

}
