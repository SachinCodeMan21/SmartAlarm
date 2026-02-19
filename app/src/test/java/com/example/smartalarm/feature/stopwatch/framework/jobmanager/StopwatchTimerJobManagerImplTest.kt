package com.example.smartalarm.feature.stopwatch.framework.jobmanager

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.StopwatchTimerJobManagerImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.StopwatchTimerJobManagerImpl] class, which manages the stopwatch timer job.
 * These tests ensure the correct behavior of starting, stopping, and updating the stopwatch at regular intervals.
 *
 * The tests simulate the passage of time using [kotlinx.coroutines.test.runTest] and [advanceTimeBy], and mock dependencies such as the
 * [com.example.smartalarm.feature.stopwatch.domain.facade.contract.StopwatchUseCases] and [com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper] using MockK.
 *
 * The main functionalities tested include:
 * - Correct periodic updates of the stopwatch every 50ms.
 * - Prevention of multiple simultaneous start calls.
 * - Correct job cancellation and update stopping when the job is stopped.
 * - Ensuring the job does nothing when the stopwatch is not running.
 *
 * Dependencies:
 * - [com.example.smartalarm.feature.stopwatch.domain.facade.contract.StopwatchUseCases] (mocked): A use case for updating lap times and fetching stopwatch states.
 * - [com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper] (mocked): A helper to simulate elapsed real time.
 * - [mockOnUpdate]: A mocked suspend function that simulates the update callback for the stopwatch state.
 *
 * **Tests:**
 * - `test start updates stopwatch periodically`: Verifies that the stopwatch updates its state at regular intervals when the timer starts.
 * - `test start does nothing if already running`: Verifies that calling `start` again when the stopwatch is already running does nothing.
 * - `test stop cancels the stopwatch timer job`: Verifies that calling `stop` cancels the timer job and stops further updates.
 * - `test does nothing if stopwatch is not running`: Verifies that when the stopwatch is not running, no updates occur when the timer starts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StopwatchTimerJobManagerImplTest {
/*
    private lateinit var stopwatchTimerJobManager: StopwatchTimerJobManagerImpl
    private val stopwatchUseCase: StopwatchUseCases = mockk()
    private val clockProvider: SystemClockHelper = mockk()
    private val mockOnUpdate: suspend (StopwatchModel) -> Unit = mockk(relaxed = true)

    private val initialStopwatch = StopwatchModel(
        id = 1,
        startTime = 0L,
        elapsedTime = 0L,
        isRunning = true,
        lapTimes = emptyList(),
        lapCount = 0,
        endTime = 0L
    )

    private val updatedStopwatch = initialStopwatch.copy(elapsedTime = 100L)

    @Before
    fun setUp() {
        // Initialize the StopwatchTimerJobManager with mock dependencies
        stopwatchTimerJobManager = StopwatchTimerJobManagerImpl(stopwatchUseCase, clockProvider)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    *//**
     * Tests that the timer starts correctly and updates the stopwatch every 50ms.
     *//*
    @Test
    fun `test start updates stopwatch periodically`() = runTest {

        // Arrange: Mock the current stopwatch state and the clock provider's elapsed time
        coEvery { clockProvider.elapsedRealtime() } returnsMany listOf(
            100L,
            150L,
            200L
        )  // Simulate time passing
        coEvery { stopwatchUseCase.updateLapTimes(initialStopwatch) } returns emptyList()

        val scope = this
        stopwatchTimerJobManager.start(scope, { initialStopwatch }, mockOnUpdate)

        // Advance time by 100ms, which should trigger two updates (50ms each).
        advanceTimeBy(100)

        stopwatchTimerJobManager.stop()

        // Assert: The onUpdate callback should have been called with the updated stopwatch
        coVerify(exactly = 2) { mockOnUpdate(any()) }

    }

    *//**
     * Tests that the timer does not start if it is already running.
     *//*
    @Test
    fun `test start does nothing if already running`() = runTest {

        // Arrange: Start the stopwatch timer job
        val scope = this
        coEvery { clockProvider.elapsedRealtime() } returnsMany listOf(
            100L,
            150L,
            200L
        )  // Simulate time passing
        coEvery { stopwatchUseCase.updateLapTimes(initialStopwatch) } returns emptyList()


        // Act: Try to start the job again while it is already running
        stopwatchTimerJobManager.start(scope, { initialStopwatch }, mockOnUpdate)
        stopwatchTimerJobManager.start(scope, { initialStopwatch }, mockOnUpdate)

        // Advance time by 100ms, but the second call to start should not trigger any update
        advanceTimeBy(100)

        stopwatchTimerJobManager.stop()

        // Assert: The onUpdate callback should have been called only once
        coVerify(exactly = 2) { mockOnUpdate(any()) }
    }

    *//**
     * Tests that the timer stops when `stop` is called.
     *//*
    @Test
    fun `test stop cancels the stopwatch timer job`() = runTest {

        // Arrange: Mock the current stopwatch state and start the stopwatch timer job
        coEvery { clockProvider.elapsedRealtime() } returns 100L
        coEvery { stopwatchUseCase.updateLapTimes(initialStopwatch) } returns emptyList()

        val scope = this
        stopwatchTimerJobManager.start(scope, { initialStopwatch }, mockOnUpdate)

        // Advance time to trigger some updates
        advanceTimeBy(100)

        // Act: Stop the stopwatch timer job
        stopwatchTimerJobManager.stop()

        // Assert: The onUpdate callback should not be called after stopping the job
        coVerify(exactly = 2) { mockOnUpdate(updatedStopwatch) }
    }

    *//**
     * Tests that the stopwatch timer does nothing if the stopwatch is not running.
     *//*
    @Test
    fun `test does nothing if stopwatch is not running`() = runTest {

        // Arrange: Create a stopped stopwatch
        val stoppedStopwatch = initialStopwatch.copy(isRunning = false)

        // Act: Start the stopwatch timer job with a stopwatch that is not running
        stopwatchTimerJobManager.start(this, { stoppedStopwatch }, mockOnUpdate)

        // Advance time, but no updates should occur since the stopwatch is not running
        advanceTimeBy(100)

        // Assert: The onUpdate callback should not be called since the stopwatch is not running
        coVerify(exactly = 0) { mockOnUpdate(any()) }
    }*/
}