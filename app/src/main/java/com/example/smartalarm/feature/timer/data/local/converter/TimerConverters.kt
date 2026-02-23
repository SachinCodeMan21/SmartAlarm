package com.example.smartalarm.feature.timer.data.local.converter

import androidx.room.TypeConverter
import com.example.smartalarm.feature.timer.domain.model.TimerStatus

/**
 * Persistence bridge for custom Timer data types.
 *
 * **Why this exists:**
 * SQLite only supports a limited set of primitive types (INTEGER, TEXT, BLOB, etc.).
 * These converters allow us to use type-safe Kotlin objects like [TimerStatus] in our
 * entities while storing them as searchable strings in the database.
 *
 * **Constraint Management:**
 * By converting Enums to Strings, we ensure the database remains human-readable
 * during debugging while maintaining the strict type-safety of our domain layer.
 */
class TimerConverters {

    /**
     * Serializes [TimerStatus] into a String for database storage.
     * * **Why:** Maps the Enum constant to its [String] representation to satisfy
     * SQLite's TEXT storage requirement.
     */
    @TypeConverter
    fun fromStatus(status: TimerStatus): String = status.name

    /**
     * Deserializes a String from the database back into a [TimerStatus].
     * * **Why:** Restores the type-safe Enum from persisted text.
     * * **Safety Note:** If the database contains a value that no longer exists
     * in the Enum (due to a refactor), this will throw an [IllegalArgumentException].
     */
    @TypeConverter
    fun toStatus(value: String): TimerStatus = TimerStatus.valueOf(value)
}