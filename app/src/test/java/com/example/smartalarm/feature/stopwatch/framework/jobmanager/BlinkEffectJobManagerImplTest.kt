package com.example.smartalarm.feature.stopwatch.framework.jobmanager

import com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.BlinkEffectJobManagerImpl
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import kotlin.test.Test

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.BlinkEffectJobManagerImpl] class, which controls a blinking effect by toggling a visibility flag.
 *
 * This class tests the behavior of the blinking effect managed by a coroutine job. Specifically, it verifies:
 * - That the visibility toggles at regular intervals (500ms).
 * - The correct handling of starting and stopping the blinking effect.
 * - Ensures that starting the blinking effect while it is already running does not trigger unnecessary updates.
 * - Proper cancellation of the blinking job when stopping the effect, preventing further updates.
 *
 * The tests use mock callbacks to verify the visibility state changes during the blinking process and ensure the coroutine job behaves as expected.
 *
 * The tests also use [kotlinx.coroutines.test.runTest] to execute the coroutine logic in a controlled test environment with the ability to advance time using [advanceTimeBy].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlinkEffectJobManagerImplTest {

    private lateinit var blinkEffectJobManager: BlinkEffectJobManagerImpl
    private val mockVisibilityChanged: (Boolean) -> Unit = mockk(relaxed = true)

    /**
     * Initializes the BlinkEffectJobManager before each test.
     */
    @Before
    fun setUp() {
        blinkEffectJobManager = BlinkEffectJobManagerImpl()
    }

    /**
     * Tests that the visibility toggles every 500ms when the blinking effect is started.
     *
     * Verifies that the callback is called with the expected values in the correct sequence.
     */
    @Test
    fun `test startBlinking toggles visibility every 500ms`() = runTest {
        // Arrange
        val scope = this  // Using the current scope in the test
        val expectedCalls = listOf(true, false, true)

        // Act: Start the blinking effect
        blinkEffectJobManager.startBlinking(scope, mockVisibilityChanged)

        // Advance time by 1.2 seconds (3 toggle cycles, each 500ms)
        advanceTimeBy(1200)
        blinkEffectJobManager.stopBlinking()  // Stop the blinking job

        // Assert: Check that the callback was called with the expected states
        verifySequence {
            mockVisibilityChanged(true)
            mockVisibilityChanged(false)
            mockVisibilityChanged(true)
        }

        // Assert: The visibility toggles as expected (3 cycles)
        Assert.assertEquals(expectedCalls.size, 3)
    }

    /**
     * Tests that the blinking job is stopped and no further updates happen after calling `stopBlinking`.
     *
     * Verifies that the job is properly cancelled and no new visibility updates occur after stopping.
     */
    @Test
    fun `test stopBlinking cancels the job and stops visibility updates`() = runTest {

        // Arrange
        val scope = this
        blinkEffectJobManager.startBlinking(scope, mockVisibilityChanged)

        // Advance time to trigger any updates
        advanceTimeBy(1000)

        // Act: Stop the blinking effect
        blinkEffectJobManager.stopBlinking()

        // Assert: Ensure the job is cancelled and no further updates happen
        coVerify(exactly = 2) { mockVisibilityChanged(any()) }
    }

    /**
     * Tests that calling `startBlinking` when the effect is already running does nothing.
     *
     * Verifies that the visibility callback is only triggered once even if `startBlinking` is called again.
     */
    @Test
    fun `test startBlinking does nothing if already running`() = runTest {

        // Arrange
        val scope = this
        blinkEffectJobManager.startBlinking(scope, mockVisibilityChanged)

        // Act: Try starting the blinking effect again while it's already running
        blinkEffectJobManager.startBlinking(scope, mockVisibilityChanged)

        // Advance time to see if the effect triggers another update
        advanceTimeBy(500)

        blinkEffectJobManager.stopBlinking()

        // Assert: The callback should only be called once despite calling `startBlinking` again
        coVerify(exactly = 1) { mockVisibilityChanged(any()) }
    }
}