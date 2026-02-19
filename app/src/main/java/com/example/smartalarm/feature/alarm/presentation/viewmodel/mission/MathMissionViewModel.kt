package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.MathMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.MathMissionUiModel
import com.example.smartalarm.feature.alarm.utility.ArithmeticQuestionGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling the logic behind a math-based mission in the app.
 *
 * This ViewModel manages the state of the math mission, handles user input, and provides feedback
 * based on the user's responses. It handles starting the mission, generating arithmetic questions,
 * validating answers, and updating the UI accordingly. It also controls the flow of the mission
 * across rounds and provides effects (e.g., mission completion) to be observed by the UI.
 *
 * Key responsibilities:
 * - Initialize and manage the state for the math mission (rounds, questions, user input).
 * - Handle user actions (e.g., submitting an answer) and provide feedback.
 * - Handle the logic for progressing through the rounds and completing the mission.
 * - Communicate with the UI through the `uiState` and `uiEffect` flows.
 *
 * @property resourceProvider The provider for retrieving localized strings for the UI.
 * @property numberFormatter Formatter for formatting numbers according to the locale.
 * @property arithmeticQuestionGenerator A generator for creating arithmetic questions based on the mission difficulty.
 */
@HiltViewModel
class MathMissionViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter,
    private val arithmeticQuestionGenerator: ArithmeticQuestionGenerator
) : ViewModel()
{

    private val _uiState = MutableStateFlow(MathMissionUiModel())
    val uiState: StateFlow<MathMissionUiModel> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MissionEffect>(0)
    val uiEffect = _uiEffect.asSharedFlow()

    private var mathMission: Mission? = null

    private var currentRound = 1

    private var currentAnswer = 0.0


    /**
     * Returns the total number of rounds for the current math mission.
     *
     * - Retrieves the `rounds` property from the `mathMission` object if available.
     * - Returns 0 if `mathMission` is null.
     *
     * @return The total number of rounds as an integer. Defaults to 0 if `mathMission` is null.
     */
    private fun getTotalMathRound() : Int{
        return mathMission?.rounds?:0
    }


    /**
     * Emits a UI effect to notify the UI of a mission-related event.
     *
     * @param effect The [MissionEffect] to be posted, representing a specific mission event.
     */
    private fun postEffect(effect: MissionEffect){
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    // ---------------------------------------------------------------------
    //  Math Mission Event Handler
    // ---------------------------------------------------------------------

    /**
     * Handles various events related to the math mission.
     *
     * Based on the event type, the appropriate method is called to manage the mission's flow:
     * - [MathMissionEvent.StartMission]: Starts the mission with the provided mission data.
     * - [MathMissionEvent.SubmitAnswer]: Submits the user's answer to be processed.
     * - [MathMissionEvent.MissionCompleted]: Emits a "MissionCompleted" effect, signaling the end of the mission.
     *
     * @param event The event to handle, which can be one of [MathMissionEvent.StartMission],
     *              [MathMissionEvent.SubmitAnswer], or [MathMissionEvent.MissionCompleted].
     */
    fun handleEvent(event: MathMissionEvent){
        when(event){
            is MathMissionEvent.StartMission -> startMission(event.mission)
            is MathMissionEvent.SubmitAnswer -> submitAnswer(event.ans)
            is MathMissionEvent.MissionCompleted -> postEffect(MissionEffect.MissionCompleted)
        }
    }


    /**
     * Initializes the mission and starts the first round.
     *
     * Sets the provided [Mission] object and triggers the start of the first round.
     *
     * @param mission The mission object that contains the details for the math mission.
     */
    private fun startMission(mission: Mission) {
        this.mathMission = mission
        startNextRound()
    }


    /**
     * Starts the next round of the mission.
     *
     * - Checks if the current round exceeds the total rounds; if so, marks the mission as completed.
     * - Updates the UI state with the round number, instructions, and enables input for the user.
     * - Generates a new math question for the round.
     */
    private fun startNextRound() {

        if (currentRound > getTotalMathRound()) {
            missionCompleted()
            return
        }

        _uiState.update {
            it.copy(
                roundText = resourceProvider.getString(
                    R.string.round_text, numberFormatter.formatLocalizedNumber(currentRound.toLong(), false),
                    numberFormatter.formatLocalizedNumber(getTotalMathRound().toLong(), false)
                ),
                instruction = resourceProvider.getString(R.string.solve_the_math_problem),
                instructionColor = R.color.colorOnSurface,
                isSubmitEnabled = true,
                isInputEnabled = true,
                clearInput = false,
                statusImageRes = R.drawable.start_img
            )
        }

        generateQuestion()

    }

    /**
     * Generates a new arithmetic question based on the mission's difficulty.
     *
     * - Uses the [arithmeticQuestionGenerator] to generate a question and its answer.
     * - Updates the UI state with the generated question.
     * - Stores the correct answer for later validation.
     */
    private fun generateQuestion() {
        val (question, answer) = arithmeticQuestionGenerator.generateQuestion(mathMission?.difficulty ?: Difficulty.EASY)
        currentAnswer = answer
        _uiState.update { it.copy(question = question) }
    }


    /**
     * Submits the user's answer and provides feedback based on the input.
     *
     * - Validates the user's input, ensuring it is a valid number.
     * - Displays appropriate feedback for blank inputs, invalid formats, or correct/incorrect answers.
     * - Advances to the next round on correct answers, or resets the round for retry on wrong answers.
     * - Disables input during feedback and clears the input field after feedback.
     *
     * @param userInput The user's answer to the current math question.
     */
    private fun submitAnswer(userInput: String) = viewModelScope.launch {
        val userAnswer = userInput.toIntOrNull()

        when {

            userInput.isBlank() -> {
                showFeedback(
                    "Please enter an answer!",
                    android.R.color.holo_red_light,
                    R.drawable.error_img
                )
                delay(1000)
                resetForRetry()
            }

            userAnswer == null -> {
                showFeedback(
                    "Invalid number format!",
                    android.R.color.holo_red_light,
                    R.drawable.error_img
                )
                delay(1000)
                resetForRetry()
            }

            userAnswer == currentAnswer.toInt() -> {
                showFeedback(
                    "Correct!",
                    R.color.green,
                    R.drawable.correct_img,
                    isEnabled = false,
                    clearInput = true
                )
                delay(1000)
                currentRound++
                startNextRound()
            }

            else -> {
                showFeedback(
                    "Wrong answer!",
                    android.R.color.holo_red_light,
                    R.drawable.error_img,
                    isEnabled = false,
                    clearInput = true
                )
                delay(1000)
                resetForRetry()
            }
        }
    }





    // ---------------------------------------------------------------------
    //  Math Mission Simple State Updates
    // ---------------------------------------------------------------------

    /**
     * Updates the UI with feedback message, color, status image, and input states.
     *
     * @param message The feedback message to display.
     * @param colorRes The resource ID for the text color.
     * @param imageRes The resource ID for the status image.
     * @param isEnabled Whether the submit button and input field should be enabled (default is true).
     * @param clearInput Whether the input field should be cleared (default is false).
     */
    private fun showFeedback(message: String, colorRes: Int, imageRes: Int, isEnabled: Boolean = true, clearInput: Boolean = false) {
        _uiState.update {
            it.copy(
                instruction = message,
                instructionColor = colorRes,
                statusImageRes = imageRes,
                isSubmitEnabled = isEnabled,
                isInputEnabled = isEnabled,
                clearInput = clearInput
            )
        }
    }

    /**
     * Resets the UI to prepare for retrying the current round.
     *
     * - Sets the instruction back to "Solve the math problem."
     * - Resets the input and submit button states to enabled.
     * - Clears any feedback messages or images.
     */
    private fun resetForRetry() {
        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.solve_the_math_problem),
                instructionColor = R.color.colorOnSurface,
                statusImageRes = R.drawable.start_img,
                isSubmitEnabled = true,
                isInputEnabled = true,
                clearInput = false
            )
        }
    }

    /**
     * Updates the UI to indicate that the mission has been completed.
     *
     * - Sets a congratulatory message and the "Mission Completed" label.
     * - Disables the input and submit button.
     * - Marks the mission as completed and clears the input field.
     */
    private fun missionCompleted() {
        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.great_job_you_completed_all_rounds),
                roundText = resourceProvider.getString(R.string.mission_completed),
                isSubmitEnabled = false,
                isInputEnabled = false,
                clearInput = true,
                statusImageRes = R.drawable.start_img
            )
        }

        viewModelScope.launch {
            postEffect(MissionEffect.MissionCompleted)
        }
    }

}