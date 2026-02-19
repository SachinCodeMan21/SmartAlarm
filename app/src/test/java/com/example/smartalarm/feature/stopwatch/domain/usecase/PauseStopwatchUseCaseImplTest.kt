package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.PauseStopwatchUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.PauseStopwatchUseCaseImpl] class, which handles pausing a running stopwatch.
 *
 * This test suite ensures that the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.PauseStopwatchUseCaseImpl] behaves correctly under different scenarios:
 * 1. When the stopwatch is already paused, it should return the same stopwatch without making changes.
 * 2. When the stopwatch is running, it should pause the stopwatch, update its state, and save it to the repository.
 * 3. If saving the paused stopwatch fails, an error result should be returned.
 *
 * The tests use the [io.mockk.impl.annotations.MockK] library for mocking dependencies and the [kotlinx.coroutines.test.runTest] function for coroutine testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PauseStopwatchUseCaseImplTest {

/*    @MockK
    private lateinit var repository: StopWatchRepository

    @InjectMockKs
    private lateinit var pauseStopwatchUseCase: PauseStopwatchUseCaseImpl


    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // PauseStopwatchUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenStopwatchIsAlreadyPaused_shouldReturn_successWithSameStopwatch() = runTest {
        // Arrange
        val pausedStopwatch = createStopwatch(isRunning = false)

        // Act
        val result = pauseStopwatchUseCase.invoke(pausedStopwatch)

        // Assert
        Assert.assertEquals(Result.Success(pausedStopwatch), result)
        coVerify(exactly = 0) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenStopwatchIsRunning_shouldPauseAndReturn_successWithUpdatedStopwatch() = runTest {
        // Arrange
        val startedStopwatch = createStopwatch(isRunning = true)
        val pausedStopwatch = startedStopwatch.copy(isRunning = false)

        coEvery { repository.saveStopwatch(any()) } returns Result.Success(Unit)

        // Act
        val result = pauseStopwatchUseCase.invoke(startedStopwatch)

        // Assert
        Assert.assertEquals(Result.Success(pausedStopwatch), result)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenRepositorySaveFails_shouldReturn_errorResult() = runTest {
        // Arrange
        val startedStopwatch = createStopwatch(isRunning = true)
        val pausedStopwatch = startedStopwatch.copy(isRunning = false)
        val exception = RuntimeException("Fails to pause stopwatch")

        coEvery { repository.saveStopwatch(any()) } throws exception

        // Act
        val result = pauseStopwatchUseCase.invoke(startedStopwatch)

        // Assert
        Assert.assertTrue(result is Result.Error)
        Assert.assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }



    //====================================================
    // Helper Method
    //====================================================

    *//**
     * Creates a [com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel] with specified attributes.
     *//*
    private fun createStopwatch(
        isRunning: Boolean,
        startTime: Long = 0L,
        elapsedTime: Long = 0L,
        endTime: Long? = null
    ) = StopwatchModel(
        id = 1,
        startTime = startTime,
        elapsedTime = elapsedTime,
        isRunning = isRunning,
        lapTimes = emptyList(),
        lapCount = 0,
        endTime = endTime ?: 0L
    )*/
}