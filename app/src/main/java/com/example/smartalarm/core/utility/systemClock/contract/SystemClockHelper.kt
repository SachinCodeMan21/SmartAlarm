package com.example.smartalarm.core.utility.systemClock.contract

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime


/**
 * Interface defining methods for working with system time and date in different formats.
 *
 * This interface provides methods to retrieve the current system time, elapsed time, and
 * to generate `ZonedDateTime` objects based on the system clock. It abstracts different
 * system clock functionalities needed for managing alarms and time-based actions in the application.
 */
interface SystemClockHelper {

    /**
     * Gets the current system time in milliseconds.
     *
     * @return Current system time in milliseconds since the epoch (Unix time).
     */
    fun getCurrentTime(): Long

    /**
     * Gets the elapsed real time in milliseconds since the system was booted.
     *
     * @return Elapsed time in milliseconds since device boot.
     */
    fun elapsedRealtime(): Long

    /**
     * Gets the current date and time in the default system time zone.
     *
     * @return A [ZonedDateTime] representing the current date and time.
     */
    fun getZonedDateTime(): ZonedDateTime

    /**
     * Creates a [ZonedDateTime] from a given date, time, and time zone.
     *
     * @param date The date for the ZonedDateTime.
     * @param alarmTime The time of day for the ZonedDateTime.
     * @param zone The time zone for the ZonedDateTime.
     * @return A [ZonedDateTime] for the specified date, time, and zone.
     */
    fun createZonedDateTime(date: LocalDate, alarmTime: LocalTime, zone: ZoneId): ZonedDateTime
}