package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.PauseStopwatchUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import org.junit.Before
import org.junit.Test




@OptIn(ExperimentalCoroutinesApi::class)
class PauseStopwatchUseCaseImplTest {
/*
    @MockK
    private lateinit var repository: StopwatchRepository

    private lateinit var pauseStopwatchUseCase: PauseStopwatchUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        pauseStopwatchUseCase = PauseStopwatchUseCaseImpl(repository)
    }

    // ====================================================
    // PauseStopwatchUseCase Test Scenarios
    // ====================================================

    @Test
    fun `invoke when stopwatch is already paused should return success and not persist`() = runTest {
        // Arrange: Repository returns a stopwatch that is already isRunning = false
        val pausedStopwatch = createStopwatch(isRunning = false)
        every { repository.getCurrentStopwatchState() } returns pausedStopwatch

        // Act
        val result = pauseStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        // Verify we checked the state but never called save because it was already paused
        coVerify(exactly = 0) { repository.persistStopwatch(any()) }
    }

    @Test
    fun `invoke when stopwatch is running should pause and persist updated state`() = runTest {
        // Arrange: Repository returns a running stopwatch
        val startedStopwatch = createStopwatch(isRunning = true)
        every { repository.getCurrentStopwatchState() } returns startedStopwatch
        coEvery { repository.persistStopwatch(any()) } returns MyResult.Success(Unit)

        // Act
        val result = pauseStopwatchUseCase.invoke()

        // Assert
        val capturedStopwatch = slot<StopwatchModel>()
        coVerify(exactly = 1) { repository.persistStopwatch(capture(capturedStopwatch)) }

        // Verify the captured state has isRunning = false
        assertThat(capturedStopwatch.captured.isRunning).isFalse()
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `invoke when repository save fails should return mapped error`() = runTest {
        // Arrange
        val startedStopwatch = createStopwatch(isRunning = true)
        every { repository.getCurrentStopwatchState() } returns startedStopwatch

        // Simulate a database failure
        coEvery { repository.persistStopwatch(any()) } throws RuntimeException("Persistence failed")

        // Act
        val result = pauseStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        coVerify(exactly = 1) { repository.persistStopwatch(any()) }
    }

    // ====================================================
    // Helper Method
    // ====================================================

    private fun createStopwatch(
        isRunning: Boolean,
        startTime: Long = 1000L,
        elapsedTime: Long = 5000L
    ) = StopwatchModel(
        startTime = startTime,
        elapsedTime = elapsedTime,
        isRunning = isRunning,
        lapTimes = emptyList(),
        lapCount = 0
    )*/
}