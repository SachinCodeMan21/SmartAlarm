package com.example.smartalarm.feature.alarm.domain.enums

import com.example.smartalarm.R


/**
 * Enum class representing the difficulty levels available for a feature (e.g., alarm missions, games, tasks).
 *
 * Each difficulty level is associated with:
 * - A [sliderValue] which represents the difficulty as a floating-point value (commonly used in UI controls like SeekBars or Sliders).
 * - A [labelResId] which is a reference to a string resource used for displaying the human-readable label for the difficulty level in the UI.
 *
 * Enum Constants:
 * - [EASY] - sliderValue = 0f, label = "Easy"
 * - [NORMAL] - sliderValue = 1f, label = "Normal"
 * - [HARD] - sliderValue = 2f, label = "Hard"
 * - [EXPERT] - sliderValue = 3f, label = "Expert"
 *
 * This enum can be used to map slider values to their corresponding difficulty levels and display them appropriately.
 *
 * Example usage:
 * ```kotlin
 * val difficulty = Difficulty.fromSliderValue(slider.value)
 * // Use difficulty.labelResId to fetch the string resource associated with the difficulty.
 * ```
 *
 * @property sliderValue The float value that represents the difficulty in UI sliders or SeekBars.
 * @property labelResId The resource ID of the label string corresponding to the difficulty level.
 */
enum class Difficulty(val sliderValue: Float, val labelResId: Int) {

    EASY(0f, R.string.difficulty_easy),
    NORMAL(1f, R.string.difficulty_normal),
    HARD(2f, R.string.difficulty_hard),
    EXPERT(3f, R.string.difficulty_expert);

    companion object {
        /**
         * Returns the [Difficulty] corresponding to the given slider float value.
         *
         * If the value does not match any valid index (0–3), it defaults to [EASY].
         * This method helps map a floating-point value from a slider (or other input) to a specific difficulty.
         *
         * @param value The slider float value to map to a difficulty level.
         * @return The corresponding [Difficulty] enum, or [EASY] if the value is invalid.
         */
        fun fromSliderValue(value: Float): Difficulty =
            entries.getOrNull(value.toInt()) ?: EASY
    }

}