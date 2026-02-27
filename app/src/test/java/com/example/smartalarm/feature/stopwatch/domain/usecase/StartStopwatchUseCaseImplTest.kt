package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.StartStopwatchUseCaseImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test


@OptIn(ExperimentalCoroutinesApi::class)
class StartStopwatchUseCaseImplTest {
/*
    @MockK
    private lateinit var clockProvider: SystemClockHelper

    @MockK
    private lateinit var repository: StopwatchRepository

    private lateinit var startStopwatchUseCase: StartStopwatchUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        startStopwatchUseCase = StartStopwatchUseCaseImpl(clockProvider,repository)
    }

    // ====================================================
    // StartStopwatchUseCase Test Scenarios
    // ====================================================

    @Test
    fun `invoke when already running should return Success and not modify repository`() = runTest {
        // Arrange
        val runningStopwatch = StopwatchModel(isRunning = true)
        every { repository.getCurrentStopwatchState() } returns runningStopwatch

        // Act
        val result = startStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        verify { clockProvider wasNot Called }
        coVerify(exactly = 0) { repository.persistStopwatch(any()) }
    }

    @Test
    fun `invoke when not running should calculate start time correctly and persist`() = runTest {
        // Arrange
        val currentTime = 10000L
        val previousElapsed = 2000L
        val initialStopwatch = StopwatchModel(
            isRunning = false,
            elapsedTime = previousElapsed
        )

        every { repository.getCurrentStopwatchState() } returns initialStopwatch
        every { clockProvider.getCurrentTime() } returns currentTime
        coEvery { repository.persistStopwatch(any()) } returns MyResult.Success(Unit)

        // Act
        val result = startStopwatchUseCase.invoke()

        // Assert
        val capturedStopwatch = slot<StopwatchModel>()
        coVerify(exactly = 1) { repository.persistStopwatch(capture(capturedStopwatch)) }

        // Logic Check: startTime = current - elapsed (10000 - 2000 = 8000)
        assertThat(capturedStopwatch.captured.startTime).isEqualTo(8000L)
        assertThat(capturedStopwatch.captured.isRunning).isTrue()
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `invoke when repository save fails should return mapped error`() = runTest {
        // Arrange
        val stopwatch = StopwatchModel(isRunning = false)
        every { repository.getCurrentStopwatchState() } returns stopwatch
        every { clockProvider.getCurrentTime() } returns 5000L

        val exception = RuntimeException("Disk I/O error")
        coEvery { repository.persistStopwatch(any()) } throws exception

        // Act
        val result = startStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        // Verify mapper logic was triggered (Result.Error contains the mapped result)
        coVerify(exactly = 1) { repository.persistStopwatch(any()) }
    }*/
}