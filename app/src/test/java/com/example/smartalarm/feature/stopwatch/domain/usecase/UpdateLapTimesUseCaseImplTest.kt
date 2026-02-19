package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
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
@OptIn(ExperimentalCoroutinesApi::class)
class UpdateLapTimesUseCaseImplTest {

/*    @MockK
    private lateinit var clockProvider: SystemClockHelper

    @InjectMockKs
    private lateinit var updateLapTimesUseCase: UpdateLapTimesUseCaseImpl

    private val startTime = 1000L
    private val currentTime = 5000L
    private val elapsedSinceStart = currentTime - startTime

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { clockProvider.elapsedRealtime() } returns currentTime
    }


    //====================================================
    // UpdateLapTimesUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenLapTimesIsEmpty_shouldReturnEmptyList() {
        // Arrange
        val stopwatch = createStopwatchWithLaps()

        // Act
        val result = updateLapTimesUseCase.invoke(stopwatch)

        // Assert
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun invoke_whenStopwatchHasLaps_shouldUpdateLastLapWithElapsedAndEndTimes() {
        // Arrange
        val lap1 = StopWatchLapModel(1, 0L, 1000L, 1000L)
        val lap2 = StopWatchLapModel(2, 1000L, 500L, 1500L) // This lap will be updated
        val stopwatch = createStopwatchWithLaps(lap1, lap2)

        // Act
        val result = updateLapTimesUseCase.invoke(stopwatch)
        val updatedLap = result.last()
        val expectedElapsed = elapsedSinceStart - lap2.lapStartTime

        // Assert
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(lap1, result.first()) // First lap unchanged
        Assert.assertEquals(expectedElapsed, updatedLap.lapElapsedTime)
        Assert.assertEquals(elapsedSinceStart, updatedLap.lapEndTime)
    }

    @Test
    fun invoke_whenUpdatingLastLap_shouldMaintainLapIndexAndStartTime() {
        // Arrange
        val lap = StopWatchLapModel(1, 0L, 1000L, 1000L)
        val stopwatch = createStopwatchWithLaps(lap)

        // Act
        val result = updateLapTimesUseCase.invoke(stopwatch)
        val updated = result.first()

        // Assert
        Assert.assertEquals(lap.lapIndex, updated.lapIndex)
        Assert.assertEquals(lap.lapStartTime, updated.lapStartTime)
        Assert.assertEquals(elapsedSinceStart - lap.lapStartTime, updated.lapElapsedTime)
        Assert.assertEquals(elapsedSinceStart, updated.lapEndTime)
    }

    //====================================================
    // Helper Method
    //====================================================

    *//**
     * Helper to create a stopwatch with specified laps for testing.
     *//*
    private fun createStopwatchWithLaps(vararg laps: StopWatchLapModel): StopwatchModel {
        return StopwatchModel(
            id = 1,
            startTime = startTime,
            elapsedTime = 0L,
            isRunning = true,
            lapTimes = laps.toList(),
            lapCount = laps.size,
            endTime = 0L
        )
    }*/
}