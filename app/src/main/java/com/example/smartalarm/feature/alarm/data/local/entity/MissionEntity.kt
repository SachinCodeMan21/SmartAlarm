package com.example.smartalarm.feature.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty

/**
 * Represents a mission associated with an alarm, stored in the database.
 *
 * Maps to the `mission_table` in the database.
 * Each mission references an [AlarmEntity] via a foreign key on [alarmId].
 *
 * @property id The unique identifier for the mission (auto-generated).
 * @property alarmId The foreign key linking this mission to a specific alarm.
 * @property type The type of mission as a string (e.g., "memory", "typing").
 * @property difficulty The difficulty level as a string (e.g., "EASY", "HARD").
 * @property rounds The number of rounds or attempts required for the mission.
 * @property iconResId The resource ID for the mission icon.
 * @property isCompleted Whether the mission has been completed.
 */
@Entity(
    tableName = "mission_table",
    foreignKeys = [ForeignKey(
        entity = AlarmEntity::class,
        parentColumns = ["id"],
        childColumns = ["alarmId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("alarmId")]
)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alarmId: Int,
    val type: String,
    val difficulty: String,
    val rounds: Int,
    val iconResId: Int,
    val isCompleted: Boolean
)