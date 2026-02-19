package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Represents a single lap within a stopwatch session.
 *
 * Each lap entry is linked to a parent stopwatch via the [stopwatchId]. The combination of [stopwatchId] and [lapIndexId]
 * uniquely identifies a lap within the stopwatch session.
 *
 * @property stopwatchId The ID of the parent stopwatch, referenced from the [StopWatchEntity].
 * @property lapIndexId The sequential index of the lap, starting from 1. This value is unique per stopwatch session.
 * @property lapStartTime The timestamp (in milliseconds) indicating when this lap started.
 * @property lapElapsedTime The total duration of this lap, in milliseconds.
 * @property lapEndTime The timestamp (in milliseconds) indicating when this lap ended.
 */
@Entity(
    tableName = "stopwatch_lap_table",
    foreignKeys = [ForeignKey(
        entity = StopWatchEntity::class,
        parentColumns = ["id"],
        childColumns = ["stopwatchId"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["stopwatchId", "lapIndexId"]
)
data class StopWatchLapEntity(
    val stopwatchId: Int,
    val lapIndexId: Int,
    val lapStartTime: Long,
    val lapElapsedTime: Long,
    val lapEndTime: Long
)
