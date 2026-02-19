package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import android.os.VibrationEffect
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.event.mission.StepMissionEvent
import app.cash.turbine.test
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StepMissionViewModelTest {

    private lateinit var viewModel: StepMissionViewModel

    private lateinit var systemClockHelper: SystemClockHelper
    private lateinit var vibrationManager: VibrationManager

    private val testMission = Mission(
        type = MissionType.Step,
        difficulty = Difficulty.NORMAL,
        rounds = 5, // Need 5 steps to complete
        iconResId = 0,
        isCompleted = false
    )

    @Before
    fun setup() {
        systemClockHelper = mockk(relaxed = true)
        vibrationManager = mockk(relaxed = true)
        viewModel = StepMissionViewModel(systemClockHelper,vibrationManager)

        // Freeze time at 1000ms initially
        every { systemClockHelper.getCurrentTime()} returns 1000L
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `initialize sets totalSteps correctly`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(5, state.totalSteps)
            assertEquals(0, state.stepCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `step with low acceleration is ignored`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission))

        // Low acceleration
        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(5f))
        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        verify { vibrationManager wasNot Called }
    }


/*    @Test
    fun `step too soon after previous is ignored`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission))

        every { systemClockHelper.getCurrentTime() } returnsMany listOf(1000L, 1200L) // only 200ms apart

        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(15f))
        viewModel.handleEvent(StepMissionEvent.StepDetected) // first valid
        advanceUntilIdle()

        viewModel.handleEvent(StepMissionEvent.StepDetected) // too soon → ignored
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(1, state.stepCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { vibrationManager.vibrateOneShot(any(), any()) }
    }

    @Test
    fun `valid step increments count and vibrates`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission))

        every { systemClockHelper.getCurrentTime() } returns 1000L

        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(15f))
        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(1, state.stepCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) {
            vibrationManager.vibrateOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE)
        }
    }

    @Test
    fun `steps after delay are counted separately`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission))

        every { systemClockHelper.getCurrentTime() } returnsMany listOf(1000L, 2000L)

        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(15f))

        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(2, state.stepCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 2) { vibrationManager.vibrateOneShot(any(), any()) }
    }

    @Test
    fun `reaching total steps emits MissionCompleted after delay`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission.copy(rounds = 2)))

        every { systemClockHelper.getCurrentTime() } returnsMany listOf(1000L, 2000L, 3000L)

        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(15f))

        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.handleEvent(StepMissionEvent.StepDetected) // completes
        advanceUntilIdle()

        advanceTimeBy(600L)
        advanceUntilIdle() // critical!

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(MissionEffect.MissionCompleted, effect)
        }
    }

    @Test
    fun `extra steps after completion are ignored`() = runTest {
        viewModel.handleEvent(StepMissionEvent.InitializeMission(testMission.copy(rounds = 1)))

        every { systemClockHelper.getCurrentTime() } returns 1000L

        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(15f))
        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()
        advanceTimeBy(600L)
        advanceUntilIdle()

        every { systemClockHelper.getCurrentTime() } returns 5000L
        viewModel.handleEvent(StepMissionEvent.AccelerationChanged(20f))
        viewModel.handleEvent(StepMissionEvent.StepDetected)
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(1, state.stepCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { vibrationManager.vibrateOneShot(any(), any()) }
    }*/
}
