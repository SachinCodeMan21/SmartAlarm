package com.example.smartalarm.feature.alarm.data.local.converters

import androidx.room.TypeConverter
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import java.time.LocalTime


/**
 * Provides [TypeConverter] functions for storing complex types in Room.
 *
 * Room only supports a limited set of column types natively. This class
 * converts non-primitive types used in `AlarmEntity` into formats that
 * can be stored in the database and back.
 *
 * Conversions included:
 * - [LocalTime] ↔ [String]
 * - [Set]<[DayOfWeek]> ↔ [String]
 *
 * Usage:
 * Add this class to your Room database `@TypeConverters` annotation
 * to allow Room to automatically convert these types when reading/writing.
 */
class AlarmConverters {

    /**
     * Converts a [LocalTime] to its string representation for Room storage.
     *
     * @param time The [LocalTime] instance to convert.
     * @return String representation of the time in ISO-8601 format (e.g., "08:30").
     */
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    /**
     * Converts a stored string back to a [LocalTime].
     *
     * @param time String representation of time stored in the database.
     * @return Parsed [LocalTime] instance.
     */
    @TypeConverter
    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    /**
     * Converts a set of [DayOfWeek] to a comma-separated string for storage.
     *
     * @param days Set of [DayOfWeek] representing selected days.
     * @return Comma-separated string of day names (e.g., "MONDAY,WEDNESDAY,FRIDAY").
     */
    @TypeConverter
    fun fromDaySet(days: Set<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

    /**
     * Converts a stored comma-separated string back to a set of [DayOfWeek].
     *
     * @param data Comma-separated string of day names from the database.
     * @return Set of [DayOfWeek]. Returns empty set if the string is blank.
     */
    @TypeConverter
    fun toDaySet(data: String): Set<DayOfWeek> {
        return if (data.isBlank()) emptySet() else data.split(",").map { DayOfWeek.valueOf(it) }.toSet()
    }

}