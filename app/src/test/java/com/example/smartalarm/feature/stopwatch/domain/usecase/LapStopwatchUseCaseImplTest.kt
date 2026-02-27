package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.LapStopwatchUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import kotlin.test.Test
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import kotlinx.coroutines.test.runTest


@OptIn(ExperimentalCoroutinesApi::class)
class LapStopwatchUseCaseImplTest {
/*
    @MockK
    private lateinit var clockProvider: SystemClockHelper
    @MockK
    private lateinit var repository: StopwatchRepository

    private lateinit var lapStopwatchUseCase: LapStopwatchUseCaseImpl

    private val startTime = 1000L
    private val currentTime = 5000L
    private val elapsedSinceStart = currentTime - startTime

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        lapStopwatchUseCase = LapStopwatchUseCaseImpl(repository, clockProvider)

        // Default clock behavior
        every { clockProvider.getCurrentTime() } returns currentTime
    }

    //====================================================
    // Test Scenarios
    //====================================================

    @Test
    fun `invoke when stopwatch not running should return Success and not persist`() = runTest {
        // Arrange
        val stopwatch = createBaseStopwatch(isRunning = false)
        every { repository.getCurrentStopwatchState() } returns stopwatch

        // Act
        val result = lapStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 0) { repository.persistStopwatch(any()) }
    }

    @Test
    fun `invoke when stopwatch running and no laps should add two laps (init + new) and persist`() = runTest {
        // Arrange: Start with 0 laps
        val stopwatch = createBaseStopwatch(isRunning = true, lapTimes = emptyList())
        every { repository.getCurrentStopwatchState() } returns stopwatch
        coEvery { repository.persistStopwatch(any()) } returns MyResult.Success(Unit)

        // Act
        val result = lapStopwatchUseCase.invoke()

        // Assert
        val capturedStopwatch = slot<StopwatchModel>()
        coVerify(exactly = 1) { repository.persistStopwatch(capture(capturedStopwatch)) }

        val laps = capturedStopwatch.captured.lapTimes
        assertThat(laps).hasSize(2)
        // Check first lap (initialization lap)
        assertThat(laps[0].lapIndex).isEqualTo(1)
        assertThat(laps[0].lapElapsedTimeMillis).isEqualTo(elapsedSinceStart)
        // Check second lap (the actual "new" lap)
        assertThat(laps[1].lapIndex).isEqualTo(2)
        assertThat(laps[1].lapStartTimeMillis).isEqualTo(elapsedSinceStart)

        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `invoke when stopwatch running with existing laps should add only one new lap`() = runTest {
        // Arrange: Start with 1 existing lap
        val existingLap = StopwatchLapModel(1, 0L, 500L, 500L)
        val stopwatch = createBaseStopwatch(isRunning = true, lapTimes = listOf(existingLap))

        every { repository.getCurrentStopwatchState() } returns stopwatch
        coEvery { repository.persistStopwatch(any()) } returns MyResult.Success(Unit)

        // Act
        lapStopwatchUseCase.invoke()

        // Assert
        val capturedStopwatch = slot<StopwatchModel>()
        coVerify(exactly = 1) { repository.persistStopwatch(capture(capturedStopwatch)) }

        val laps = capturedStopwatch.captured.lapTimes
        assertThat(laps).hasSize(2) // 1 existing + 1 new
        assertThat(laps.last().lapIndex).isEqualTo(2)
        assertThat(laps.last().lapStartTimeMillis).isEqualTo(elapsedSinceStart)
    }

    @Test
    fun `invoke when repository fails should return Error via ExceptionMapper`() = runTest {
        // Arrange
        val stopwatch = createBaseStopwatch(isRunning = true)
        every { repository.getCurrentStopwatchState() } returns stopwatch
        coEvery { repository.persistStopwatch(any()) } throws RuntimeException("DB Error")

        // Act
        val result = lapStopwatchUseCase.invoke()

        // Assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    //====================================================
    // Helpers
    //====================================================

    private fun createBaseStopwatch(
        isRunning: Boolean,
        lapTimes: List<StopwatchLapModel> = emptyList()
    ) = StopwatchModel(
        startTime = startTime,
        isRunning = isRunning,
        lapTimes = lapTimes,
        lapCount = lapTimes.size
    )*/
}