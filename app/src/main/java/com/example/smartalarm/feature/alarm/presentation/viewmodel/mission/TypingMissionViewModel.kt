package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.TypingMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.TypingMissionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.utility.extension.toLocalizedString
import kotlinx.coroutines.CoroutineDispatcher

/**
 * ViewModel for managing the typing mission logic in a typing challenge game.
 *
 * This ViewModel handles the overall flow of the typing mission, including initializing the mission,
 * tracking the current round, managing user input, providing feedback on correctness, and ending the mission.
 * It also provides methods for handling UI state changes, updating the current round and paragraph,
 * and providing color-coded feedback to the user as they type.
 *
 * Key features:
 * - Initialization of mission data (sentences and rounds).
 * - Handling of user input with real-time feedback.
 * - Display of round-specific information.
 * - Management of UI state and effects, including feedback on correctness and transition between rounds.
 * - End-of-mission handling, signaling the mission's completion.
 *
 * **Properties:**
 * - `uiState`: Holds the current UI state of the typing mission (e.g., current paragraph, input text, feedback).
 * - `uiEffect`: A flow that emits one-time effects, like the mission completion effect.
 * - `paragraphs`: A shuffled list of the sentences/paragraphs for the typing mission.
 * - `totalRounds`: The total number of rounds in the mission.
 * - `currentRound`: The current round of the mission.

 * **Public Methods:**
 * - `handleEvent`: Receives events from the UI and delegates them to the appropriate internal methods.
 * - `initializeMission`: Initializes the typing mission, including loading the sentences and setting up the rounds.
 * - `startRound`: Starts a new round, setting up the appropriate UI state.
 * - `onInputChanged`: Handles changes to the input text, updating the UI with feedback and progress.
 * - `checkInput`: Checks if the user's input is correct, providing feedback and transitioning to the next round or resetting input.
 * - `startNextRound`: Starts the next round after correct input.
 * - `endMission`: Ends the mission when all rounds are completed and triggers the mission completion effect.
 * - `createOverlaySpannable`: Creates a SpannableStringBuilder with colored feedback based on the user input vs correct text.
 * - `resetInputAndFeedback`: Resets the input field and feedback after a short delay, preparing for the next input.

 * **Internal Methods:**
 * - `createOverlaySpannable`: Used internally to generate color-coded overlays for input text and feedback.
 * - `resetInputAndFeedback`: Clears the input and resets the feedback UI.
 */
@HiltViewModel
class TypingMissionViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    // UI State and Effect Flows
    private val _uiState = MutableStateFlow(TypingMissionUiModel())
    val uiState: StateFlow<TypingMissionUiModel> = _uiState.asStateFlow()

    private val _uiEffect = Channel<MissionEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    // Mission Data
    private lateinit var paragraphs: List<String>   // List of paragraphs for the mission
    private var totalRounds: Int = 0                // Total rounds in the mission
    private var currentRound: Int = 0               // Current round of the mission

    // ---------------------------------------
    // Public Methods
    // ---------------------------------------

    /**
     * Handle incoming events from the UI.
     * Based on the event type, call the corresponding private method.
     */
    fun handleEvent(event: TypingMissionEvent) {
        when (event) {
            is TypingMissionEvent.InitializeMission -> initializeMission(event.mission)
            is TypingMissionEvent.StartMission -> startRound()
            is TypingMissionEvent.InputTextChanged -> onInputChanged(event.input)
            is TypingMissionEvent.CheckIsInputCorrect -> checkInput(event.input)
        }
    }

    // ---------------------------------------
    // Mission Initialization
    // ---------------------------------------

    /**
     * Initialize the mission by setting up the list of paragraphs
     * and total rounds.
     */
    private fun initializeMission(mission: Mission) {
        paragraphs = resourceProvider.getStringArray(R.array.typing_mission_sentences).toList().shuffled()
        totalRounds = mission.rounds
        currentRound++
    }

    // ---------------------------------------
    // User Input Handling
    // ---------------------------------------

    /**
     * Handle text input change.
     * Creates a spannable overlay that highlights the differences between input and correct text.
     */
    private fun onInputChanged(input: String) {
        viewModelScope.launch {
            val correctText = paragraphs[currentRound]
            val overlay = withContext(defaultDispatcher) {
                createOverlaySpannable(input, correctText)
            }
            _uiState.value = _uiState.value.copy(
                inputText = input,
                overlaySpannable = overlay
            )
        }
    }

    /**
     * Check if the user input is correct.
     * Provides feedback and proceeds accordingly.
     */
    private fun checkInput(input: String) {

        val correctText = paragraphs[currentRound]

        when {
            input.isEmpty() -> {
                _uiState.value = _uiState.value.copy(feedback = resourceProvider.getString(R.string.please_type_the_paragraph))
                resetInputAndFeedback()
            }

            input != correctText -> {
                _uiState.value = _uiState.value.copy(
                    feedback = resourceProvider.getString(R.string.incorrect_try_again),
                    isSubmitEnabled = false
                )
                resetInputAndFeedback()
            }

            else -> startNextRound()  // Input is correct, start the next round
        }
    }

    // ---------------------------------------
    // Round Handling
    // ---------------------------------------

    /**
     * Start a new round. If all rounds are completed, end the mission.
     */
    private fun startRound() {
        if (currentRound > totalRounds) {
            endMission()
            return
        }

        val currentText = paragraphs[currentRound]
        _uiState.value = _uiState.value.copy(
            roundText = resourceProvider.getString(
                R.string.round_text,
                currentRound.toLong().toLocalizedString(),
                totalRounds.toLong().toLocalizedString()
            ),
            currentParagraph = currentText,
            inputText = "",
            overlaySpannable = SpannableStringBuilder(""),
            feedback = "",
            isInputEnabled = true,
            isSubmitEnabled = true
        )
    }

    /**
     * Proceed to the next round after correct input.
     */
    private fun startNextRound() {
        currentRound++
        _uiState.value = _uiState.value.copy(feedback = resourceProvider.getString(R.string.correct))
        startRound()
    }

    // ---------------------------------------
    // Mission Completion
    // ---------------------------------------

    /**
     * End the mission and notify UI with a completion effect.
     */
    private fun endMission() {
/*        _uiState.value = _uiState.value.copy(
            roundText = resourceProvider.getString(R.string.mission_completed),
            currentParagraph = resourceProvider.getString(R.string.great_job_you_completed_all_rounds),
            isInputEnabled = false,
            isSubmitEnabled = false,
            feedback = "",
            overlaySpannable = SpannableStringBuilder("")
        )*/
        viewModelScope.launch {
            _uiEffect.send(MissionEffect.MissionCompleted)
        }
    }

    // ---------------------------------------
    // Overlay Creation for Input Feedback
    // ---------------------------------------

    /**
     * Create a spannable string with colored feedback for the input vs correct text.
     * The input will be highlighted in blue for correct characters, red for incorrect characters,
     * and light gray for characters that haven't been typed yet.
     */
    private fun createOverlaySpannable(input: String, correctText: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        for (i in correctText.indices) {
            if (i < input.length) {
                val typedChar = input[i]
                val correctChar = correctText[i]

                if (typedChar == correctChar) {
                    builder.append(typedChar)
                    builder.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    builder.append(typedChar)
                    builder.setSpan(
                        ForegroundColorSpan(Color.RED),
                        i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else {
                builder.append(correctText[i])
                builder.setSpan(
                    ForegroundColorSpan(Color.LTGRAY),
                    i, i + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return builder
    }

    // ---------------------------------------
    // Reset State
    // ---------------------------------------

    /**
     * Reset the input and feedback after a delay.
     */
    private fun resetInputAndFeedback() {
        viewModelScope.launch {
            delay(1000) // 1 second delay
            _uiState.value = _uiState.value.copy(
                inputText = "",
                overlaySpannable = SpannableStringBuilder(""),
                feedback = "",
                isSubmitEnabled = true
            )
        }
    }
}
