package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import androidx.annotation.ColorRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.MemoryMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.MemoryMissionUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * ViewModel for the Memory Mission feature in the app.
 *
 * This ViewModel handles the logic and state management for a memory-based game or challenge,
 * where the user has to memorize glowing squares and then select them correctly from a grid.
 * The game progresses through multiple rounds, with each round increasing the difficulty.
 * The ViewModel manages state such as the current round, the squares that are glowing, and user interactions with the squares.
 *
 * The flow of the game includes:
 * 1. Initializing the mission with a grid size based on difficulty and a number of rounds.
 * 2. Displaying glowing squares for the user to memorize.
 * 3. Allowing the user to select the squares they remember.
 * 4. Providing feedback for correct and incorrect selections.
 * 5. Handling retries and round transitions, including tracking the number of incorrect attempts.
 * 6. Managing a countdown timer for the memorization phase.
 *
 * Key Components:
 * - **UI State**: The `MemoryMissionUIModel` holds the current state of the UI, including the grid colors, instructions, and countdown text.
 * - **Mission State**: Tracks the state of the mission, such as the current round, the glowing squares, the selected squares, and the number of incorrect attempts.
 * - **Event Handling**: Responds to user actions, such as selecting squares, initializing the mission, and starting the mission.
 * - **Feedback Mechanism**: Provides visual feedback for correct and incorrect selections, including instructions and color changes.
 * - **Countdown Timer**: Controls the countdown during the memorization phase, which is triggered when the glowing squares are shown.
 *
 * @property resourceProvider The resource provider for retrieving strings and colors for UI updates.
 * @property defaultDispatcher The default dispatcher for coroutine operations.
 */
@HiltViewModel
class MemoryMissionViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel()
{

    // ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    companion object {

        /** Delay for showing feedback messages (e.g., correct/wrong selection) in milliseconds. */
        const val FEEDBACK_DELAY_MS = 1000L

        /** Maximum number of incorrect attempts before the round resets. */
        const val MAX_INCORRECT_ATTEMPTS = 3

        /** Countdown duration in seconds for memorizing glowing squares. */
        const val MEMORIZATION_COUNTDOWN_SECONDS = 3
    }

    // ---------------------------------------------------------------------
    // UI State & Effects
    // ---------------------------------------------------------------------
    private val _uiState = MutableStateFlow(MemoryMissionUIModel())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MissionEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    // ---------------------------------------------------------------------
    // Mission State
    // ---------------------------------------------------------------------
    private val glowingSquares = mutableListOf<Int>()
    private val selectedSquares = mutableSetOf<Int>()
    private var memorizationCountdownJob: Job? = null
    private var incorrectAttemptCount = 0

    /** Total number of squares in the grid. */
    val totalSquares: Int get() = _uiState.value.totalSquares

    /** Grid dimension derived from the total number of squares (e.g., 9 → 3x3). */
    val gridSize: Int get() = sqrt(totalSquares.toDouble()).toInt()


    // ---------------------------------------------------------------------
    // Event Handler
    // ---------------------------------------------------------------------

    /**
     * Handles events from the UI layer.
     *
     * @param event The [MemoryMissionEvent] to handle.
     */
    fun handleEvent(event: MemoryMissionEvent) {
        when (event) {
            is MemoryMissionEvent.InitializeMission -> initializeMission(event.mission)
            is MemoryMissionEvent.StartMission -> startMission()
            is MemoryMissionEvent.SquareSelected -> handleSquareClick(event.index)
        }
    }


    // ---------------------------------------------------------------------
    // Mission Initialization
    // ---------------------------------------------------------------------

    /**
     * Initializes the mission UI state based on the given mission.
     *
     * Sets the grid size, total squares, and initial neutral colors.
     *
     * @param mission The [Mission] object containing difficulty and rounds info.
     */
    private fun initializeMission(mission: Mission) {
        val gridSize = when (mission.difficulty) {
            Difficulty.EASY -> 3
            Difficulty.NORMAL -> 4
            Difficulty.HARD -> 5
            Difficulty.EXPERT -> 6
        }
        val totalSquares = gridSize * gridSize

        _uiState.value = _uiState.value.copy(
            totalSquares = totalSquares,
            totalRounds = mission.rounds,
            squareColors = List(totalSquares) { R.color.neutral }
        )
    }



    // ---------------------------------------------------------------------
    // Round Display
    // ---------------------------------------------------------------------

    /**
     * Starts a new mission round.
     * - Prepares the round by setting up necessary state.
     * - Displays the glowing squares for the user to memorize.
     */
    fun startMission() {
        prepareRound()
        showGlowingSquares()
    }


    /**
     * Prepares the next round of the mission.
     * - Resets the selected squares and incorrect attempt count.
     * - Increments the current round.
     * - Randomly selects a subset of squares to glow, based on a fraction of the total squares.
     */
    private fun prepareRound() {
        selectedSquares.clear()
        incorrectAttemptCount = 0
        _uiState.update { it.copy(currentRound = it.currentRound + 1) }

        glowingSquares.clear()
        glowingSquares.addAll(
            (0 until totalSquares)
                .shuffled()
                .take((totalSquares / 3f).roundToInt())
        )
    }

    /**
     * Displays the glowing squares for the user to memorize.
     * - Updates the UI state with instructions to memorize the glowing squares.
     * - Sets the square colors: glowing squares are highlighted, others remain neutral.
     * - Disables interaction with squares while they are glowing.
     * - Starts a countdown before enabling square selection for the user.
     */
    private fun showGlowingSquares() {
        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.memorize_the_glowing_squares),
                instructionColor = R.color.purple,
                squareColors = List(totalSquares) { i -> if (i in glowingSquares) R.color.glow else R.color.neutral },
                isSquaresEnabled = false
            )
        }

        runCountdown { enableSquareSelection() }
    }

    /**
     * Enables square selection after the glowing squares phase.
     * - Clears the countdown text and updates the instruction to prompt the user to select the glowing squares.
     * - Resets square colors to neutral and enables interaction with the squares.
     */
    private fun enableSquareSelection() {
        _uiState.update {
            it.copy(
                countdownText = null,
                instruction = resourceProvider.getString(R.string.select_the_squares_that_glowed),
                instructionColor = R.color.colorOnSurface,
                squareColors = List(totalSquares) { R.color.neutral },
                isSquaresEnabled = true
            )
        }
    }


    /**
     * Starts the next round of the mission.
     * - If the current round is the last round, emits a "MissionCompleted" effect.
     * - Otherwise, prepares for the next round and shows the glowing squares.
     */
    private fun startNextRound() {
        if (_uiState.value.currentRound >= _uiState.value.totalRounds) {
            viewModelScope.launch { _uiEffect.emit(MissionEffect.MissionCompleted) }
            return
        }
        prepareRound()
        showGlowingSquares()
    }






    // ---------------------------------------------------------------------
    // Handle User Square Selection Interaction
    // ---------------------------------------------------------------------

    /**
     * Handles the user clicking on a square.
     * - If square selection is disabled, the click is ignored.
     * - Toggles the selection state of the clicked square.
     * - Updates square color based on whether the square is glowing or not.
     * - If all glowing squares are selected correctly, triggers the correct selection handler.
     * - If a non-glowing square is selected, handles the incorrect selection.
     */
    private fun handleSquareClick(index: Int) {
        if (!_uiState.value.isSquaresEnabled) return

        if (selectedSquares.contains(index)) {
            selectedSquares.remove(index)
            updateSquareColor(index, R.color.neutral)
            return
        }

        selectedSquares.add(index)

        if (index in glowingSquares) {
            updateSquareColor(index, R.color.selected)
            if (selectedSquares.containsAll(glowingSquares)) handleCorrectSelection()
        } else {
            handleIncorrectSelection(index)
        }
    }

    /**
     * Handles the scenario when all glowing squares are selected correctly.
     * - Updates the instruction to display "Correct" with a green color.
     * - Disables further square selection.
     * - Waits for a short delay before starting the next round.
     */
    private fun handleCorrectSelection() {

        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.correct),
                instructionColor = R.color.green,
                isSquaresEnabled = false
            )
        }

        viewModelScope.launch {
            delay(FEEDBACK_DELAY_MS)
            startNextRound()
        }
    }


    /**
     * Handles the scenario when an incorrect square is selected.
     * - Increments the incorrect attempt count and updates the clicked square's color to indicate error.
     * - Updates the instruction to display an error message and disables square selection.
     * - After a delay, either retries the current round or triggers a retry after the max incorrect attempts.
     */
    private fun handleIncorrectSelection(index: Int) {
        incorrectAttemptCount++
        updateSquareColor(index, R.color.incorrect)

        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.wrong_square_selected),
                instructionColor = R.color.error,
                isSquaresEnabled = false
            )
        }

        viewModelScope.launch {
            delay(FEEDBACK_DELAY_MS)
            if (incorrectAttemptCount >= MAX_INCORRECT_ATTEMPTS) {
                retryCurrentRoundAfterMaxIncorrect()
            } else {
                retryAfterIncorrectAttempt()
            }
        }
    }






    // ---------------------------------------------------------------------
    // Incorrect Attempt Handling
    // ---------------------------------------------------------------------

    /**
     * Retries the square selection after an incorrect attempt.
     * - Clears the selected squares and resets the UI to prompt the user to select the glowing squares again.
     * - Enables square selection and updates the instruction to indicate the action.
     */
    private fun retryAfterIncorrectAttempt() {
        selectedSquares.clear()
        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.select_the_squares_that_glowed),
                instructionColor = R.color.colorOnSurface,
                squareColors = List(totalSquares) { R.color.neutral },
                isSquaresEnabled = true
            )
        }
    }


    /**
     * Retries the current round after reaching the maximum number of incorrect attempts.
     * - Clears the user's selections and resets the incorrect attempt count.
     * - Updates the UI with an error message and disables square selection.
     * - After a short delay, shows the glowing squares again and starts a countdown for memorization.
     */
    private fun retryCurrentRoundAfterMaxIncorrect() {
        // Clear user selections but keep the same glowing squares
        selectedSquares.clear()
        incorrectAttemptCount = 0

        // Update UI to show feedback
        _uiState.update {
            it.copy(
                instruction = resourceProvider.getString(R.string.too_many_incorrect_attempts),
                instructionColor = R.color.error,
                isSquaresEnabled = false
            )
        }

        viewModelScope.launch {
            // Short delay for feedback
            delay(FEEDBACK_DELAY_MS)

            // Show the same glowing squares again
            _uiState.update {
                it.copy(
                    instruction = resourceProvider.getString(R.string.memorize_the_glowing_squares),
                    instructionColor = R.color.purple,
                    squareColors = List(totalSquares) { i ->
                        if (i in glowingSquares) R.color.glow else R.color.neutral
                    }
                )
            }

            // Start countdown for memorization
            runCountdown { enableSquareSelection() }
        }
    }






    // ---------------------------------------------------------------------
    // Utility Methods
    // ---------------------------------------------------------------------

    /**
     * Updates the color of a square at the specified index.
     * - Creates a mutable copy of the current square colors and updates the color at the given index.
     * - The updated colors list is then applied to the UI state to reflect the change.
     */
    private fun updateSquareColor(index: Int, @ColorRes colorRes: Int) {
        val updatedColors = _uiState.value.squareColors.toMutableList().apply {
            this[index] = colorRes
        }
        _uiState.update { it.copy(squareColors = updatedColors) }
    }


    /**
     * Starts a countdown for the memorization phase.
     * - Cancels any existing countdown job.
     * - Initiates a countdown from the specified number of seconds and updates the UI with the remaining time.
     * - Once the countdown finishes, the provided callback (`onComplete`) is invoked, and the countdown text is cleared.
     */
    private fun runCountdown(onComplete: () -> Unit) {
        memorizationCountdownJob?.cancel()
        memorizationCountdownJob = viewModelScope.launch(defaultDispatcher) {
            for (i in MEMORIZATION_COUNTDOWN_SECONDS downTo 1) {
                _uiState.update { it.copy(countdownText = i.toString()) }
                delay(1000)
            }
            onComplete()
            _uiState.update { it.copy(countdownText = null) }
        }
    }


    // Public getter for test purposes
    @VisibleForTesting(VisibleForTesting.NONE)
    internal val glowingSquaresForTesting: List<Int>
        get() = glowingSquares

}