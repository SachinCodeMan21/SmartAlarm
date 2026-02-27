package com.example.smartalarm.core.utility.systemClock.impl

import android.os.SystemClock
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/**
 * Implementation of [SystemClockHelper] that provides actual system clock operations.
 *
 * This class retrieves the current time, elapsed real time, and creates or retrieves
 * `ZonedDateTime` objects. It uses Java's system clock and date-time APIs to provide the
 * functionality needed to handle time and date operations for scheduling and managing alarms.
 */
class SystemClockHelperImpl @Inject constructor() : SystemClockHelper {

    /**
     * Returns the current time in milliseconds.
     *
     * @return The current time in milliseconds since the Unix epoch.
     */
    override fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Returns the elapsed real time in milliseconds since the system was booted.
     *
     * @return The elapsed time since the device was powered on.
     */
    override fun elapsedRealtime(): Long {
        return SystemClock.elapsedRealtime()
    }

    /**
     * Returns the current date and time in the default system time zone.
     *
     * @return A [ZonedDateTime] representing the current date and time in the system's time zone.
     */
    override fun getZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.now()
    }

    /**
     * Creates a [ZonedDateTime] for a given date, time, and time zone.
     *
     * @param date The date for the ZonedDateTime.
     * @param alarmTime The time of day for the ZonedDateTime.
     * @param zone The time zone for the ZonedDateTime.
     * @return A [ZonedDateTime] instance corresponding to the specified date, time, and zone.
     */
    override fun createZonedDateTime(date: LocalDate, alarmTime: LocalTime, zone: ZoneId): ZonedDateTime {
        return ZonedDateTime.of(date, alarmTime, zone)
    }

    override fun formatLocalTime(utcMillis: Long, offsetSeconds: Int): String {
        return runCatching {
            val zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds)
            val localTime = Instant.ofEpochMilli(utcMillis).atOffset(zoneOffset)
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            localTime.format(formatter)
        }.getOrElse { "--:--" }
    }

}