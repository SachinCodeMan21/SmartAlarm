package com.example.smartalarm.feature.stopwatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a stopwatch record in the local database.
 *
 * This entity stores metadata about a single stopwatch session,
 * including start/end time, total elapsed time, lap count, and its state.
 *
 * @property id Unique ID for the stopwatch record. Defaults to 1 since typically only one stopwatch is stored.
 * @property startTime Timestamp in milliseconds when the stopwatch started.
 * @property elapsedTime Total time elapsed during the stopwatch session in milliseconds.
 * @property endTime Timestamp in milliseconds when the stopwatch was stopped or paused.
 * @property isRunning Indicates whether the stopwatch is currently running.
 * @property lapCount Number of laps recorded during the stopwatch session.
 */
@Entity(tableName = "stopwatch_table")
data class StopWatchEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    val startTime: Long = 0L,
    val elapsedTime: Long = 0L,
    val endTime: Long = 0L,
    val isRunning: Boolean = false,
    val lapCount: Int = 0
)