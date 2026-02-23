package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.UpdateStopwatchTickerStateUseCaseImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.UpdateLapTimesUseCaseImpl] class, responsible for updating lap times
 * in a running stopwatch.
 *
 * The tests cover the following scenarios:
 * 1. When there are no laps, an empty list is returned.
 * 2. When the stopwatch has laps, the last lap's elapsed and end times are updated correctly.
 * 3. The last lap's index and start time remain unchanged during the update process.
 *
 * The tests use [io.mockk.MockK] for mocking dependencies and [kotlinx.coroutines.test.runTest] for coroutine-based testing.
 */

class UpdateStopwatchTickerStateUseCaseImplTest {

    @MockK
    private lateinit var repository: StopwatchRepository

    @MockK
    private lateinit var clockProvider: SystemClockHelper

    private lateinit var useCase: UpdateStopwatchTickerStateUseCaseImpl

    private val startTime = 1000L
    private val currentTime = 5000L
    private val totalElapsed = currentTime - startTime // 4000ms

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        useCase = UpdateStopwatchTickerStateUseCaseImpl(repository, clockProvider)

        // Default clock behavior
        every { clockProvider.getCurrentTime() } returns currentTime
    }

    @Test
    fun `invoke when stopwatch is not running should do nothing`() {
        // Arrange
        val stopwatch = createStopwatch(isRunning = false)
        every { repository.getCurrentStopwatchState() } returns stopwatch

        // Act
        useCase.invoke()

        // Assert
        verify(exactly = 0) { repository.updateTickerState(any()) }
    }

    @Test
    fun `invoke when stopwatch running with no laps should only update total elapsed time`() {
        // Arrange
        val stopwatch = createStopwatch(isRunning = true, laps = emptyList())
        every { repository.getCurrentStopwatchState() } returns stopwatch
        every { repository.updateTickerState(any()) } just Runs

        // Act
        useCase.invoke()

        // Assert
        val captured = slot<StopwatchModel>()
        verify { repository.updateTickerState(capture(captured)) }

        assertThat(captured.captured.elapsedTime).isEqualTo(totalElapsed)
        assertThat(captured.captured.lapTimes).isEmpty()
    }

    @Test
    fun `invoke when running with laps should update the last lap duration and end time`() {
        // Arrange:
        // Lap 1: started at 0 (relative to stopwatch start), was 1000ms long.
        // Lap 2: started at 1000ms. We are now at 4000ms total elapsed.
        val lap1 = StopwatchLapModel(1, 0L, 1000L, 1000L)
        val lap2 = StopwatchLapModel(2, 1000L, 0L, 1000L)
        val stopwatch = createStopwatch(isRunning = true, laps = listOf(lap1, lap2))

        every { repository.getCurrentStopwatchState() } returns stopwatch
        every { repository.updateTickerState(any()) } just Runs

        // Act
        useCase.invoke()

        // Assert
        val captured = slot<StopwatchModel>()
        verify { repository.updateTickerState(capture(captured)) }

        val updatedLaps = captured.captured.lapTimes
        val updatedLastLap = updatedLaps.last()

        // Total elapsed is 4000. Lap 2 started at 1000.
        // Expected Lap 2 elapsed = 4000 - 1000 = 3000
        assertThat(updatedLaps.size).isEqualTo(2)
        assertThat(updatedLaps.first()).isEqualTo(lap1) // Should remain unchanged
        assertThat(updatedLastLap.lapElapsedTimeMillis).isEqualTo(3000L)
        assertThat(updatedLastLap.lapEndTimeMillis).isEqualTo(4000L)
    }

    @Test
    fun `invoke should maintain lap index and start time while updating duration`() {
        // Arrange
        val initialLap = StopwatchLapModel(lapIndex = 5, lapStartTimeMillis = 2500L, 0L, 2500L)
        val stopwatch = createStopwatch(isRunning = true, laps = listOf(initialLap))

        every { repository.getCurrentStopwatchState() } returns stopwatch
        every { repository.updateTickerState(any()) } just Runs

        // Act
        useCase.invoke()

        // Assert
        val captured = slot<StopwatchModel>()
        verify { repository.updateTickerState(capture(captured)) }

        val updated = captured.captured.lapTimes.first()
        assertThat(updated.lapIndex).isEqualTo(5)
        assertThat(updated.lapStartTimeMillis).isEqualTo(2500L)
        assertThat(updated.lapElapsedTimeMillis).isEqualTo(totalElapsed - 2500L)
    }

    // ====================================================
    // Helper Method
    // ====================================================

    private fun createStopwatch(
        isRunning: Boolean,
        laps: List<StopwatchLapModel> = emptyList()
    ) = StopwatchModel(
        startTime = startTime,
        elapsedTime = 0L,
        isRunning = isRunning,
        lapTimes = laps,
        lapCount = laps.size
    )
}