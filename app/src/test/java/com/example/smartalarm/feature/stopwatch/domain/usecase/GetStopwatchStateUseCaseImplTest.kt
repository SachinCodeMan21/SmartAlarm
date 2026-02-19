package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.GetStopwatchStateUseCaseImpl
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
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.GetStopwatchStateUseCaseImpl] class, which retrieves the state of a stopwatch
 * from the repository based on its ID.
 *
 * The tests cover the following scenarios:
 * 1. When the repository successfully returns a stopwatch model, the use case returns the model.
 * 2. When no stopwatch is found, the use case returns `null`.
 * 3. When the repository encounters an error, the use case returns the error.
 *
 * The tests use [io.mockk.impl.annotations.MockK] for mocking the repository and [kotlinx.coroutines.test.runTest] for coroutine-based testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetStopwatchStateUseCaseImplTest {
/*

    @MockK
    private lateinit var repository: StopWatchRepository

    @InjectMockKs
    private lateinit var getSavedStopwatchUseCase: GetStopwatchStateUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // GetStopwatchStateUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenStopwatchExists_shouldReturn_successWithStopwatch() = runTest {

        // Arrange
        val stopwatch = StopwatchModel(
            id = 1,
            isRunning = false,
            elapsedTime = 1000L,
            startTime = 0L
        )
        coEvery { repository.getStopwatchById(any()) } returns Result.Success(stopwatch)

        // Act
        val result = getSavedStopwatchUseCase()

        // Assert
        Assert.assertEquals(Result.Success(stopwatch), result)
        coVerify(exactly = 1) { repository.getStopwatchById(any()) }
    }

    @Test
    fun invoke_whenStopwatchDoesNotExist_shouldReturn_successWithNull() = runTest {

        // Arrange
        coEvery { repository.getStopwatchById(any()) } returns Result.Success(null)

        // Act
        val result = getSavedStopwatchUseCase()

        // Assert
        Assert.assertEquals(Result.Success(null), result)
        coVerify(exactly = 1) { repository.getStopwatchById(any()) }
    }

    @Test
    fun invoke_whenRepositoryReturnsError_shouldReturn_errorResult() = runTest {

        // Arrange
        val exception = RuntimeException("Data source failure")
        coEvery { repository.getStopwatchById(any()) } returns Result.Error(exception)

        // Act
        val result = getSavedStopwatchUseCase()

        // Assert
        Assert.assertEquals(Result.Error(exception), result)
        coVerify(exactly = 1) { repository.getStopwatchById(any()) }
    }
*/

}