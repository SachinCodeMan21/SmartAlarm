package com.example.smartalarm.feature.timer.presentation.view.statemanager

import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.timer.presentation.view.statemanager.impl.TimerInputStateManagerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [com.example.smartalarm.feature.timer.presentation.view.statemanager.impl.TimerInputStateManagerImpl], which manages the input state for a timer.
 *
 * This test class verifies the behavior of the [com.example.smartalarm.feature.timer.presentation.view.statemanager.impl.TimerInputStateManagerImpl] class by testing its core functionality,
 * including the ability to append digits, remove digits, clear input, and convert the input into formatted time or
 * milliseconds. The tests also validate the visibility of the start button based on the input state.
 *
 * The tests make use of the [io.mockk.mockk] library to mock the [com.example.smartalarm.core.utility.formatter.time.TimeFormatter] dependency, allowing isolation of the
 * logic in the `TimerInputStateManagerImpl` class without relying on actual implementations of time formatting
 * or conversion.
 */
class TimerInputStateManagerImplTest {

    private lateinit var timeFormatter: TimeFormatter
    private lateinit var timerInputStateManager: TimerInputStateManagerImpl

    /**
     * Set up the necessary mocks and instances before each test.
     * Initializes [timeFormatter] as a mocked instance and creates the [TimerInputStateManagerImpl]
     * with the mock injected.
     */
    @Before
    fun setUp() {
        // Create a mock ULocale instance
        timeFormatter = mockk()
        timerInputStateManager = TimerInputStateManagerImpl(timeFormatter)
    }

    @After
    fun tearDown() {
        // Clear all mocks
        unmockkAll()
    }


    /**
     * Verifies that the method `appendDigit()` successfully adds a digit to the input string
     * when the number of digits is less than the maximum allowed length.
     */
    @Test
    fun `appendDigit should add digit to inputDigits if not max length`() {
        // Arrange
        timerInputStateManager.appendDigit("1")

        // Act
        val result = timerInputStateManager.getInputDigitsForTesting()

        // Assert
        assertEquals("1", result)
    }

    /**
     * Verifies that the method `appendDigit()` does not append additional digits if the
     * maximum allowed length of digits is reached (i.e., 6 digits).
     */
    @Test
    fun `appendDigit should not add digit if max length is reached`() {

        // Arrange
        repeat(6) { timerInputStateManager.appendDigit("1") }

        // Act
        timerInputStateManager.appendDigit("2")

        // Assert
        assertEquals("111111", timerInputStateManager.getInputDigitsForTesting())
    }

    /**
     * Verifies that the method `removeLastDigit()` removes the last digit from the input string.
     * If the input contains "12", it should become "1" after removal.
     */
    @Test
    fun `removeLastDigit should remove the last digit`() {

        // Arrange
        timerInputStateManager.appendDigit("1")
        timerInputStateManager.appendDigit("2")

        // Act
        timerInputStateManager.removeLastDigit()

        // Assert
        assertEquals("1", timerInputStateManager.getInputDigitsForTesting())
    }

    /**
     * Verifies that the method `removeLastDigit()` does nothing when the input is empty.
     */
    @Test
    fun `removeLastDigit should do nothing if input is empty`() {
        // Act
        timerInputStateManager.removeLastDigit()

        // Assert
        assertEquals("", timerInputStateManager.getInputDigitsForTesting())
    }

    /**
     * Verifies that the method `clearInput()` resets the input digits to an empty string.
     */
    @Test
    fun `clearInput should reset inputDigits to empty`() {
        // Arrange
        timerInputStateManager.appendDigit("1")
        timerInputStateManager.appendDigit("2")

        // Act
        timerInputStateManager.clearInput()

        // Assert
        assertEquals("", timerInputStateManager.getInputDigitsForTesting())
    }

    /**
     * Verifies that the method `getFormattedTime()` returns a formatted timer string based on the input digits.
     * For example, given "123", the result should be "00h : 01m : 23s".
     */
    @Test
    fun `getFormattedTime should return formatted string when there are input digits`() {
        // Arrange
        every { timeFormatter.formatStringDigitsToTimerTextFormat("123") } returns "00h : 01m : 23s"

        // Act
        timerInputStateManager.appendDigit("1")
        timerInputStateManager.appendDigit("2")
        timerInputStateManager.appendDigit("3")

        // Assert
        val formattedTime = timerInputStateManager.getFormattedTime()
        assertEquals("00h : 01m : 23s", formattedTime)
    }

    /**
     * Verifies that the start button is visible when there are input digits.
     */
    @Test
    fun `isStartButtonVisible should return true if there are input digits`() {
        // Arrange
        timerInputStateManager.appendDigit("1")

        // Act
        val isVisible = timerInputStateManager.isStartButtonVisible()

        // Assert
        Assert.assertTrue(isVisible)
    }

    /**
     * Verifies that the start button is not visible when there are no input digits.
     */
    @Test
    fun `isStartButtonVisible should return false if there are no input digits`() {
        // Act
        val isVisible = timerInputStateManager.isStartButtonVisible()

        // Assert
        Assert.assertFalse(isVisible)
    }

    /**
     * Verifies that the method `timerInputToMillis()` converts the input digits into a valid
     * timer duration in milliseconds.
     */
    @Test
    fun `timerInputToMillis should convert input digits to milliseconds`() {
        // Arrange
        every { timeFormatter.formatStringDigitsToMillis("123") } returns 123000L

        // Act
        timerInputStateManager.appendDigit("1")
        timerInputStateManager.appendDigit("2")
        timerInputStateManager.appendDigit("3")

        // Assert
        val millis = timerInputStateManager.timerInputToMillis()
        assertEquals(123000L, millis)
    }

    /**
     * Verifies that the `getInputDigitsForTesting()` method correctly returns the internal
     * state of the input digits for testing purposes.
     */
    @Test
    fun `getInputDigitsForTesting should return correct internal state for testing`() {
        // Act
        timerInputStateManager.appendDigit("1")
        timerInputStateManager.appendDigit("2")
        timerInputStateManager.appendDigit("3")

        // Assert
        val result = timerInputStateManager.getInputDigitsForTesting()
        assertEquals("123", result)
    }
}