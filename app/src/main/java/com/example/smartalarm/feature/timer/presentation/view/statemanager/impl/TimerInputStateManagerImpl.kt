package com.example.smartalarm.feature.timer.presentation.view.statemanager.impl

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.feature.timer.presentation.view.statemanager.contract.TimerInputStateManager
import javax.inject.Inject


/**
 * Concrete implementation of [TimerInputStateManager] that uses external state handlers
 * to persist and retrieve input digits, enabling process death and configuration change resilience.
 *
 * This implementation relies on lambdas for getting and setting the input digits,
 * which should typically be connected to a [SavedStateHandle] or another persistence mechanism
 * by the owning ViewModel or component.
 *
 * @constructor Creates an instance of [TimerInputStateManagerImpl].
 */
class TimerInputStateManagerImpl @Inject constructor(
    private val timeFormatter: TimeFormatter
) : TimerInputStateManager {

    companion object{
        private const val MAX_DIGITS = 6
    }

    private var inputDigits: String = ""

    /**
     * Attempts to append a digit string to the current input digits.
     *
     * Prevents appending if doing so would exceed the maximum allowed digits.
     *
     * @param digit The digit string to append (e.g., "0", "1", "00").
     * @return `true` if the digit was successfully appended; `false` if the maximum digit count is reached.
     */
    override fun appendDigit(digit: String) {
        if (inputDigits.length >= MAX_DIGITS) return
        inputDigits += digit
    }

    /**
     * Removes the last digit from the input digits string.
     *
     * If the input is already empty, this operation has no effect.
     */
    override fun removeLastDigit() {
        if (inputDigits.isNotEmpty()) {
            inputDigits = inputDigits.dropLast(1)
        }
    }

    /**
     * Clears all digits from the input, resetting the input state to empty.
     */
    override fun clearInput() {
        inputDigits = ""
    }

    /**
     * Returns a formatted timer string based on the current input digits.
     *
     * If no input digits are present, returns a default timer text.
     *
     * @return A formatted string representing the timer (e.g., "12h : 34m : 56s").
     */
    override fun getFormattedTime(): String {
        return timeFormatter.formatStringDigitsToTimerTextFormat(inputDigits)
    }

    /**
     * Indicates whether the start button should be visible based on the input state.
     *
     * The start button is visible if there is at least one digit entered.
     *
     * @return `true` if input digits are not empty; `false` otherwise.
     */
    override fun isStartButtonVisible(): Boolean = inputDigits.isNotEmpty()

    /**
     * Converts the current input digits to a timer duration in milliseconds.
     *
     * Delegates conversion logic to an extension function for parsing.
     *
     * @return The duration in milliseconds represented by the input digits.
     */
    override fun timerInputToMillis(): Long = timeFormatter.formatStringDigitsToMillis(inputDigits)

    /**
     * Getter for inputDigits that is only exposed for testing purposes.
     * This allows unit tests to access the internal state without violating encapsulation.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    internal fun getInputDigitsForTesting(): String {
        return inputDigits
    }

}
