package com.example.smartalarm.feature.timer.presentation.view.statemanager.contract

/**
 * Interface defining the contract for managing the timer input state.
 *
 * Responsibilities include:
 * - Managing the input digits (append, remove last, clear all).
 * - Formatting the input digits into a user-friendly timer display string.
 * - Converting the input digits into a duration in milliseconds.
 * - Determining the visibility state of the start button based on input.
 * - Setting external handlers to get and set the input state (e.g., for persistence).
 */
interface TimerInputStateManager {


    /**
     * Appends a digit string to the current input.
     *
     * @param digit The digit string to append (examples: "0", "1", "00").
     * @return `true` if the digit was appended successfully; `false` if appending
     *         would exceed the maximum allowed input length.
     */
    fun appendDigit(digit: String)

    /**
     * Removes the last digit from the current input.
     *
     * If the input is empty, this operation has no effect.
     */
    fun removeLastDigit()

    /**
     * Clears all input digits, resetting the input state.
     */
    fun clearInput()

    /**
     * Returns the formatted timer text based on the current input digits.
     *
     * For example, an input string "123456" could be formatted as "12h : 34m : 56s".
     *
     * @return The formatted timer string suitable for display in the UI.
     */
    fun getFormattedTime(): String

    /**
     * Indicates whether the start button should be visible in the UI.
     *
     * Typically, the start button is visible only when there is at least one digit entered.
     *
     * @return `true` if the input is not empty; `false` otherwise.
     */
    fun isStartButtonVisible(): Boolean

    /**
     * Converts the current input digits to a timer duration in milliseconds.
     *
     * This conversion interprets the input digits as a time value (hours, minutes, seconds).
     *
     * @return The timer duration represented by the input digits, in milliseconds.
     */
    fun timerInputToMillis(): Long
}

