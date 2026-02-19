package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.StartStopwatchUseCaseImpl
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.StartStopwatchUseCaseImpl] class. This class is responsible for managing the
 * starting and stopping of a stopwatch in the application.
 *
 * The tests mock the necessary dependencies, such as the [com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper] for time retrieval
 * and the [com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository] for storing the stopwatch data. The tests ensure that the stopwatch
 * behaves correctly in different scenarios, including:
 *
 * 1. When the stopwatch is already running (it should return the same stopwatch).
 * 2. When the stopwatch is not running (it should start the stopwatch and save the updated state).
 * 3. When an error occurs during the save operation (the error should be correctly handled and returned).
 *
 * The tests use the [io.mockk.impl.annotations.MockK] library to mock dependencies and the [kotlinx.coroutines.test.runTest] function for coroutine support.
 *
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StartStopwatchUseCaseImplTest {

/*    @MockK
    private lateinit var clockProvider: SystemClockHelper

    @MockK
    private lateinit var repository: StopWatchRepository

    @InjectMockKs
    private lateinit var startStopwatchUseCase: StartStopwatchUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // StartStopwatchUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenStopwatchIsAlreadyRunning_shouldReturn_successWithSameStopwatch() = runTest {
        // Arrange
        val runningStopwatch = StopwatchModel(
            startTime = 1234L,
            elapsedTime = 1000L,
            isRunning = true
        )

        // Act
        val result = startStopwatchUseCase.invoke(runningStopwatch)

        // Assert
        TestCase.assertEquals(Result.Success(runningStopwatch), result)
        verify { clockProvider wasNot Called }
        verify { repository wasNot Called }
    }


    @Test
    fun invoke_whenStopwatchIsNotRunning_shouldStartStopwatchAndReturn_successWithUpdatedStopwatch() =
        runTest {
            // Arrange
            val currentTime = 6000L
            val stopwatch = StopwatchModel(
                startTime = 0L,
                elapsedTime = 2000L,
                isRunning = false
            )

            val expectedStartTime = currentTime - stopwatch.elapsedTime
            val startedStopwatch = stopwatch.copy(
                startTime = expectedStartTime,
                isRunning = true
            )

            every { clockProvider.elapsedRealtime() } returns currentTime
            coEvery { repository.saveStopwatch(any()) } returns Result.Success(Unit)

            // Act
            val result = startStopwatchUseCase.invoke(stopwatch)

            // Assert
            TestCase.assertEquals(Result.Success(startedStopwatch), result)
            coVerify(exactly = 1) { repository.saveStopwatch(any()) }
            verify(exactly = 1) { clockProvider.elapsedRealtime() }
        }


    @Test
    fun invoke_whenRepositorySaveFails_shouldReturn_errorResult() = runTest {

        // Arrange
        val currentTime = 6000L
        val stopwatch = StopwatchModel(
            startTime = 0L,
            elapsedTime = 2000L,
            isRunning = false
        )

        val exception = RuntimeException("Failed to save started stopwatch")
        every { clockProvider.elapsedRealtime() } returns currentTime
        coEvery { repository.saveStopwatch(any()) } throws exception

        // Act
        val result = startStopwatchUseCase.invoke(stopwatch)

        // Assert
        Assert.assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
        verify(exactly = 1) { clockProvider.elapsedRealtime() }
    }*/

}