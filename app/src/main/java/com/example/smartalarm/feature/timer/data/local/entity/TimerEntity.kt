package com.example.smartalarm.feature.timer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Timer entity stored in the database.
 * This class maps to a table named "timer_table" in the database.
 *
 * @property timerId The unique identifier for this timer. This is an auto-generated primary key.
 * @property startTime The timestamp (in milliseconds) when the timer was started.
 * @property remainingTime The remaining time (in milliseconds) for the timer, if it is running or paused.
 * @property endTime The timestamp (in milliseconds) when the timer was supposed to end.
 * @property targetTime The target time (in milliseconds) the timer is set to reach.
 * @property isTimerRunning A boolean flag indicating whether the timer is currently running.
 * @property isTimerSnoozed A boolean flag indicating whether the timer has been snoozed.
 * @property snoozedTargetTime The target time (in milliseconds) to which the timer was snoozed.
 * @property state The current state of the timer as a string. It represents the status of the timer, such as "INITIAL", "RUNNING", "PAUSED", or "STOPPED".
 *
 * This class is used as a database entity, and instances of it are stored in the "timer_table".
 */
@Entity(tableName = "timer_table")
data class TimerEntity(
    @PrimaryKey(autoGenerate = true) val timerId: Int = 0,
    val startTime: Long,
    val remainingTime: Long,
    val endTime: Long,
    val targetTime: Long,
    val isTimerRunning: Boolean,
    val isTimerSnoozed: Boolean,
    val snoozedTargetTime: Long,
    val state: String
)

