package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import app.cash.turbine.test
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.event.mission.MemoryMissionEvent
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import io.mockk.clearAllMocks
import io.mockk.every
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.assertEquals
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MemoryMissionViewModelTest {

    private lateinit var viewModel: MemoryMissionViewModel
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var testDispatcher: TestDispatcher

    private val easyMission = Mission(
        type = MissionType.Memory,
        difficulty = Difficulty.EASY,
        rounds = 2,
        iconResId = 0,
        isCompleted = false
    )

    @Before
    fun setup() {
        resourceProvider = mockk(relaxed = true)

        // Mock all strings used
        every { resourceProvider.getString(R.string.memorize_the_glowing_squares) } returns "Memorize!"
        every { resourceProvider.getString(R.string.select_the_squares_that_glowed) } returns "Select glowing ones!"
        every { resourceProvider.getString(R.string.correct) } returns "Correct!"
        every { resourceProvider.getString(R.string.wrong_square_selected) } returns "Wrong!"
        every { resourceProvider.getString(R.string.too_many_incorrect_attempts) } returns "Too many mistakes!"

        testDispatcher = StandardTestDispatcher()
        viewModel = MemoryMissionViewModel(resourceProvider,testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `initializeMission sets correct grid size per difficulty`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(9, state.totalSquares)  // 3x3
            assertEquals(2, state.totalRounds)
            cancelAndIgnoreRemainingEvents()
        }

        // Test HARD → 5x5 = 25
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission.copy(difficulty = Difficulty.HARD)))
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(25, state.totalSquares)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startMission shows glowing squares and starts countdown`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem() // after showGlowingSquares()
            assertEquals("Memorize!", state.instruction)
            assertEquals(R.color.purple, state.instructionColor)
            assertFalse(state.isSquaresEnabled)

            // At least one square should be glowing
            assertTrue(state.squareColors.count { it == R.color.glow } > 0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `countdown runs and enables selection after 3 seconds`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceUntilIdle()

        viewModel.uiState.test {
            // Should emit countdown numbers
            assertEquals("3", awaitItem().countdownText)
            assertEquals("2", awaitItem().countdownText)
            assertEquals("1", awaitItem().countdownText)

            val finalState = awaitItem()
            assertEquals(null, finalState.countdownText)
            assertEquals("Select glowing ones!", finalState.instruction)
            assertEquals(R.color.black, finalState.instructionColor)
            assertTrue(finalState.isSquaresEnabled)
            cancelAndIgnoreRemainingEvents()
        }

        advanceTimeBy(4000) // let it finish
    }

    @Test
    fun `correct selection of all glowing squares starts next round`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceUntilIdle()

        // Skip countdown
        advanceTimeBy(4000)

        // Get glowing squares from internal state (via testing getter)
        val glowing = viewModel.glowingSquaresForTesting

        // Select all glowing squares correctly
        glowing.forEach { index ->
            viewModel.handleEvent(MemoryMissionEvent.SquareSelected(index))
        }
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // skip previous states
            val feedback = awaitItem()
            assertEquals("Correct!", feedback.instruction)
            assertEquals(R.color.green, feedback.instructionColor)
            assertFalse(feedback.isSquaresEnabled)

            // After delay → next round starts
            advanceTimeBy(1500)
            val nextRound = awaitItem()
            assertEquals(2, nextRound.currentRound) // round increased
            assertTrue(nextRound.squareColors.count { it == R.color.glow } > 0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wrong square shows error and allows retry up to 3 times`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceTimeBy(4000) // skip countdown

        // Click 3 wrong squares
        repeat(3) {
            viewModel.handleEvent(MemoryMissionEvent.SquareSelected(999)) // invalid index = wrong
            advanceUntilIdle()
            advanceTimeBy(1500) // skip feedback delay
        }

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals("Too many mistakes!", state.instruction)
            assertEquals(R.color.error, state.instructionColor)

            // After delay → glowing squares shown again (same round)
            advanceTimeBy(2000)
            val retryState = awaitItem()
            assertEquals("Memorize!", retryState.instruction)
            assertEquals(R.color.purple, retryState.instructionColor)
            assertTrue(retryState.squareColors.count { it == R.color.glow } > 0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completing all rounds emits MissionCompleted effect`() = runTest {
        val singleRoundMission = easyMission.copy(rounds = 1)
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(singleRoundMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceTimeBy(4000)

        val glowing = viewModel.glowingSquaresForTesting
        glowing.forEach { viewModel.handleEvent(MemoryMissionEvent.SquareSelected(it)) }
        advanceUntilIdle()
        advanceTimeBy(1500) // feedback delay

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is MissionEffect.MissionCompleted)
        }
    }

    @Test
    fun `deselecting a square removes it from selection`() = runTest {
        viewModel.handleEvent(MemoryMissionEvent.InitializeMission(easyMission))
        viewModel.handleEvent(MemoryMissionEvent.StartMission)
        advanceTimeBy(4000)

        val glowing = viewModel.glowingSquaresForTesting.first()

        // Select
        viewModel.handleEvent(MemoryMissionEvent.SquareSelected(glowing))
        advanceUntilIdle()

        // Deselect same square
        viewModel.handleEvent(MemoryMissionEvent.SquareSelected(glowing))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(R.color.neutral, state.squareColors[glowing])
            cancelAndIgnoreRemainingEvents()
        }
    }
}