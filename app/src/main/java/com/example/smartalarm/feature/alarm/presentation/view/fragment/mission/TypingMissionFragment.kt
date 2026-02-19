package com.example.smartalarm.feature.alarm.presentation.view.fragment.mission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PASSED_MISSION_ARGS_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.FragmentTypingMissionBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.AlarmMissionEvent
import com.example.smartalarm.feature.alarm.presentation.event.mission.TypingMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.TypingMissionUiModel
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity.Companion.ARGS_ALARM_MISSION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.MyAlarmViewModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.TypingMissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue


@AndroidEntryPoint
class TypingMissionFragment : Fragment() {

    companion object {
        private const val TAG = "TypingMissionFragment"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_IS_NULL = "$TAG $PASSED_MISSION_ARGS_NULL"
    }

    private var _binding: FragmentTypingMissionBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: TypingMissionViewModel by viewModels()
    private val sharedViewModel : MyAlarmViewModel by activityViewModels()
    private var isProgrammaticChange = false



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState==null){
            val mission = requireArguments().getParcelableCompat<Mission>(ARGS_ALARM_MISSION_KEY)
                ?: throw IllegalArgumentException(MISSION_ARGS_IS_NULL)
            viewModel.handleEvent(TypingMissionEvent.InitializeMission(mission))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTypingMissionBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI interactions and begins observing state once the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState==null){
            viewModel.handleEvent(TypingMissionEvent.StartMission)
        }
        setUpListeners()
        setUpUIStateObserver()
    }

    /**
     * Clears view binding & Stop Progress Updates to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ---------------------------------------------------------------------
    // SetUp Methods
    // ---------------------------------------------------------------------

    private fun setUpListeners() {

        // Listen for user typing
        binding.etHiddenInput.doAfterTextChanged { text ->
            if (!isProgrammaticChange) {
                text?.toString()?.let {
                    viewModel.handleEvent(TypingMissionEvent.InputTextChanged(it))
                }
            }
        }

        // Submit input for validation
        binding.btnSubmit.setOnClickListener {
            val input = binding.etHiddenInput.text.toString().trim()
            viewModel.handleEvent(TypingMissionEvent.CheckIsInputCorrect(input))
        }
    }

    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { updateUIState(it) }
                }
                launch {
                    viewModel.uiEffect.collect { event ->
                        when (event) {
                            is MissionEffect.MissionCompleted -> {
                                sharedViewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
                            }
                        }
                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // Update UI State Method
    // ---------------------------------------------------------------------
    private fun updateUIState(state: TypingMissionUiModel) {
        binding.apply {
            tvRound.text = state.roundText
            tvReference.text = state.currentParagraph
            tvOverlay.text = state.overlaySpannable
            tvFeedback.text = state.feedback
            btnSubmit.isEnabled = state.isSubmitEnabled
            //typingProgressbar.progress = state.timerProgress

            // Sync hidden input field safely
            if (!isProgrammaticChange && etHiddenInput.text.toString() != state.inputText) {
                isProgrammaticChange = true
                etHiddenInput.setText(state.inputText)
                etHiddenInput.setSelection(state.inputText.length)
                isProgrammaticChange = false
            }
        }
    }

}