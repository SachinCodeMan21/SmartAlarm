package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import app.cash.turbine.test
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShakeMissionEvent
import com.google.common.base.Verify.verify
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShakeMissionViewModelTest {

    private lateinit var viewModel: ShakeMissionViewModel
    private lateinit var systemClockHelper: SystemClockHelper
    private lateinit var vibrationManager: VibrationManager

    private val testMission = Mission(
        type = MissionType.Shake,
        difficulty = Difficulty.NORMAL,
        rounds = 5,           // Need 5 shakes to complete
        iconResId = 0,
        isCompleted = false
    )

    @Before
    fun setup() {
        systemClockHelper = mockk(relaxed = true)
        vibrationManager = mockk(relaxed = true)

        // Default: time starts at 0
        every { systemClockHelper.getCurrentTime() } returns 0L

        viewModel = ShakeMissionViewModel(systemClockHelper, vibrationManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `initialize sets totalShakes correctly`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(5, state.totalShakes)
            assertEquals(0, state.shakeCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weak shake below threshold is ignored`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission))

        // Weak shake – should not change the state further
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(10.0))
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // skip the emission that came from InitializeMission
            expectNoEvents() // now we really expect nothing more
            cancelAndConsumeRemainingEvents()
        }

        verify { vibrationManager wasNot Called }
    }

    @Test
    fun `valid shake increments count and vibrates`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission))

        // First strong shake
        every { systemClockHelper.getCurrentTime() } returns 1000L
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(15.0))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.shakeCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) {
            vibrationManager.vibrateOneShot(durationMs = 100L, amplitude = 200)
        }
    }

    @Test
    fun `rapid shakes are debounced - only one counted`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission))

        every { systemClockHelper.getCurrentTime() } returnsMany listOf(1000L, 1100L) // 100ms apart < 500ms

        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(20.0)) // counted
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(25.0)) // ignored
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.shakeCount) // only 1 counted
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { vibrationManager.vibrateOneShot(any(), any()) }
    }

/*    @Test
    fun `shakes after delay are counted separately`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission))

        // First shake at t=1000
        every { systemClockHelper.getCurrentTime() } returns 1000L
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(20.0))
        advanceUntilIdle()  // <-- important!

        // Second shake at t=1600 (600ms later)
        every { systemClockHelper.getCurrentTime() } returns 1600L
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(22.0))
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // skip the emission from Initialize + first shake
            val state = awaitItem() // this is the second shake
            assertEquals(2, state.shakeCount)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 2) { vibrationManager.vibrateOneShot(any(), any()) }
    }

    @Test
    fun `reaching total shakes emits MissionCompleted after delay`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission.copy(rounds = 2)))

        every { systemClockHelper.getCurrentTime() } returnsMany listOf(1000L, 2000L)

        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(20.0))
        advanceUntilIdle()

        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(22.0))
        advanceUntilIdle()

        // Critical: advance time + process the delay()
        advanceTimeBy(1200L)
        advanceUntilIdle()  // ← This makes the delay() complete!

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(MissionEffect.MissionCompleted, effect)
        }

        viewModel.uiState.test {
            skipItems(2) // init + shake1 + shake2
            val finalState = awaitItem()
            assertEquals(2, finalState.shakeCount)
            cancelAndIgnoreRemainingEvents()
        }
    }*/

    @Test
    fun `extra shakes after completion are ignored`() = runTest {
        viewModel.handleEvent(ShakeMissionEvent.InitializeMission(testMission.copy(rounds = 1)))

        every { systemClockHelper.getCurrentTime() } returns 1000L
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(30.0)) // completes
        advanceUntilIdle()
        advanceTimeBy(1200L) // complete effect sent

        // Try to shake again
        every { systemClockHelper.getCurrentTime() } returns 3000L
        viewModel.handleEvent(ShakeMissionEvent.AccelerationChanged(40.0))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.shakeCount) // didn't go to 2
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { vibrationManager.vibrateOneShot(any(), any()) }
    }
}