package com.example.smartalarm.feature.alarm.domain.enums

// Define days of the week as enum for better clarity and usage

/**
 * Enum representing the days of the week, starting from Sunday.
 *
 * Used for alarm scheduling, recurring patterns, or calendar-based features.
 *
 * Enum constants:
 * - [SUN]: Sunday
 * - [MON]: Monday
 * - [TUE]: Tuesday
 * - [WED]: Wednesday
 * - [THU]: Thursday
 * - [FRI]: Friday
 * - [SAT]: Saturday
 */
enum class DayOfWeek {
    SUN, MON, TUE, WED, THU, FRI, SAT;

    companion object {

        /**
         * Returns the [DayOfWeek] at the given index position, or `null` if the position is invalid.
         *
         * Useful when mapping from numeric values (e.g., from a calendar widget or backend) to a day.
         *
         * @param position The index of the day (0 = SUN, 1 = MON, ..., 6 = SAT).
         * @return The corresponding [DayOfWeek], or `null` if out of bounds.
         */
        fun getDayAtPositionOrNull(position: Int): DayOfWeek? {
            return entries.getOrNull(position)
        }

    }
}
