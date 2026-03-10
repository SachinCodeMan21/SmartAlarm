package com.example.smartalarm.feature.stopwatch.utility

/**
 * Interface for stopwatch-specific time formatting.
 */
interface StopwatchTimeFormatter {

    /**
     * Formats the total duration for display.
     * @param durationMillis The elapsed time.
     * @param includeMillis If true, provides full precision (HH:mm:ss:SSS).
     */
    fun formatMainDisplay(durationMillis: Long, includeMillis: Boolean = false): String

    /**
     * Formats only the fractional second component.
     */
    fun formatFractionalSeconds(durationMillis: Long): String
}