package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.LapStopwatchUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.LapStopwatchUseCaseImpl] class, responsible for handling the lap functionality
 * of a stopwatch.
 *
 * The tests cover the following scenarios:
 * 1. When the stopwatch is not running, it returns the same stopwatch without adding a lap.
 * 2. When the stopwatch is running and has no laps, it adds a first lap and saves the updated stopwatch.
 * 3. When the stopwatch is running and has existing laps, it adds a new lap and saves the updated stopwatch.
 * 4. If saving the stopwatch fails (repository throws an exception), it returns an error result.
 *
 * The tests use [io.mockk.impl.annotations.MockK] for mocking dependencies and [kotlinx.coroutines.test.runTest] for coroutine-based testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LapStopwatchUseCaseImplTest {
/*
    @MockK
    private lateinit var clockProvider: SystemClockHelper
    @MockK
    private lateinit var repository: StopWatchRepository
    @InjectMockKs
    private lateinit var lapStopwatchUseCase: LapStopwatchUseCaseImpl

    private val startTime = 1000L
    private val currentElapsedRealtime = 5000L
    private val elapsedSinceStart = currentElapsedRealtime - startTime

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { clockProvider.elapsedRealtime() } returns currentElapsedRealtime
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // LapStopwatchUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenStopwatchNotRunning_shouldReturn_sameStopwatch() = runTest {

        // Arrange
        val stopwatch = createBaseStopwatch(isRunning = false)

        // Act
        val result = lapStopwatchUseCase.invoke(stopwatch)

        // Assert
        Assert.assertEquals(Result.Success(stopwatch), result)
        coVerify(exactly = 0) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenStopwatchRunningAndNoLaps_shouldAddFirstLapAndReturnSuccess() = runTest {

        // Arrange
        val stopwatch = createBaseStopwatch(isRunning = true)
        val expectedLapList = listOf(createFirstLap(), createNewLap())
        val expectedStopwatch = stopwatch.copy(
            lapTimes = expectedLapList,
            lapCount = expectedLapList.size
        )
        coEvery { repository.saveStopwatch(any()) } returns Result.Success(Unit)

        // Act
        val result = lapStopwatchUseCase.invoke(stopwatch)

        // Assert
        Assert.assertEquals(Result.Success(expectedStopwatch), result)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenStopwatchRunningAndExistingLaps_shouldAddNewLapAndReturnSuccess() = runTest {

        // Arrange
        val existingLap = createFirstLap()
        val stopwatch = createBaseStopwatch(isRunning = true, lapTimes = listOf(existingLap))
        val newLap = createNewLap()
        val expectedLapList = listOf(existingLap, newLap)
        val expectedStopwatch = stopwatch.copy(
            lapTimes = expectedLapList,
            lapCount = expectedLapList.size
        )

        coEvery { repository.saveStopwatch(any()) } returns Result.Success(Unit)

        // Act
        val result = lapStopwatchUseCase.invoke(stopwatch)

        // Assert
        Assert.assertEquals(Result.Success(expectedStopwatch), result)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenRepositorySaveFails_shouldReturn_error() = runTest {

        // Arrange
        val stopwatch = createBaseStopwatch(isRunning = true)
        coEvery { repository.saveStopwatch(any()) } throws RuntimeException("Save failed")

        // Act
        val result = lapStopwatchUseCase.invoke(stopwatch)

        // Assert
        Assert.assertTrue(result is Result.Error)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }



    //====================================================
    // Helper Method
    //====================================================

    private fun createBaseStopwatch(
        isRunning: Boolean,
        lapTimes: List<StopWatchLapModel> = emptyList(),
        lapCount: Int = lapTimes.size
    ) = StopwatchModel(
        id = 1,
        startTime = startTime,
        elapsedTime = 0L,
        isRunning = isRunning,
        lapTimes = lapTimes,
        lapCount = lapCount
    )

    private fun createFirstLap() = StopWatchLapModel(1, 0L, elapsedSinceStart, elapsedSinceStart)

    private fun createNewLap() = StopWatchLapModel(2, elapsedSinceStart, 0L, elapsedSinceStart)*/
}