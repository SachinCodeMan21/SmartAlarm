package com.example.smartalarm.feature.alarm.data.local.converters

import androidx.room.TypeConverter
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import java.time.LocalTime


/**
 * Type converters for the Room database to handle non-primitive types used in alarms.
 *
 * Room does not natively support types like [LocalTime] or [Set]<[DayOfWeek]>, so these
 * converters define how to store them as [String] in the database and retrieve them back.
 */
class AlarmConverters {

    /**
     * Converts a [LocalTime] instance to its [String] representation for database storage.
     *
     * Example:
     * ```kotlin
     * val time = LocalTime.of(9, 30)
     * val dbValue = fromLocalTime(time)  // "09:30"
     * ```
     *
     * @param time The [LocalTime] to convert.
     * @return A [String] representing the time.
     */
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    /**
     * Converts a [String] from the database back to a [LocalTime] instance.
     *
     * Example:
     * ```kotlin
     * val dbValue = "09:30"
     * val time = toLocalTime(dbValue)  // LocalTime.of(9, 30)
     * ```
     *
     * @param time The [String] representation of a time.
     * @return The corresponding [LocalTime] object.
     * @throws java.time.format.DateTimeParseException if the string is not a valid time format.
     */
    @TypeConverter
    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    /**
     * Converts a [Set] of [DayOfWeek] values to a comma-separated [String] for database storage.
     *
     * The set is unordered, so the order in the resulting string may vary.
     *
     * Example:
     * ```kotlin
     * val days = setOf(DayOfWeek.MON, DayOfWeek.WED)
     * val dbValue = fromDaySet(days)  // "MON,WED"
     * ```
     *
     * @param days The set of [DayOfWeek] values.
     * @return A comma-separated [String] representing the days.
     */
    @TypeConverter
    fun fromDaySet(days: Set<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

    /**
     * Converts a comma-separated [String] from the database back into a [Set] of [DayOfWeek] values.
     *
     * The input string should contain valid day names matching the [DayOfWeek] enum constants,
     * separated by commas (e.g., "MON,WED,FRI"). The function parses each day name and returns
     * a set of corresponding [DayOfWeek] values.
     *
     * **Important:** This implementation assumes that all day names in the input string are valid.
     * If the string contains an invalid day name (not matching any [DayOfWeek] constant),
     * [IllegalArgumentException] will be thrown.
     *
     * Example usage:
     * ```kotlin
     * val dbValue = "MON,WED,FRI"
     * val days: Set<DayOfWeek> = toDaySet(dbValue)
     * // Result: setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI)
     * ```
     *
     * @param data The comma-separated [String] of day names from the database.
     * @return A [Set] of [DayOfWeek] values corresponding to the input string.
     * @throws IllegalArgumentException if any day name is invalid.
     */
    @TypeConverter
    fun toDaySet(data: String): Set<DayOfWeek> {
        return if (data.isBlank()) { emptySet() } else {
            data.split(",").map { DayOfWeek.valueOf(it) }.toSet()
        }
    }

}
