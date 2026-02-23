package com.example.smartalarm.feature.timer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smartalarm.feature.timer.domain.model.TimerStatus

/**
 * Represents a persistent timer session in the database.
 *
 * **Purpose:**
 * This entity ensures that a timer survives app process death, system updates, and reboots. It stores the
 * necessary state (start time, remaining time, etc.) to accurately restore the timer’s state after interruptions.
 *
 * **Design Strategy:**
 * - Combines absolute timestamps (`endTimeMillis`) and relative durations (`remainingMillis`) for accuracy,
 *   ensuring timers are correctly restored even after system clock changes or long suspensions.
 * - Uses `endTimeMillis` to track when the timer was last stopped by the user, providing a reference for
 *   resuming or recalculating the remaining time.
 *
 * **Fields:**
 * - `id`: Auto-incremented primary key.
 * - `startTimeMillis`: Time when the timer started.
 * - `remainingMillis`: Remaining time on the timer, updated as it runs or is paused.
 * - `endTimeMillis`: Time when the user last stopped the timer. Used for resuming or recalculating the remaining time.
 * - `targetDurationMillis`: Initial user-defined duration (in milliseconds).
 * - `isTimerRunning`: Indicates if the timer is currently running.
 * - `isTimerSnoozed`: Indicates if the timer is in a snooze state.
 * - `snoozedTargetDurationMillis`: Time when the snooze period ends, if applicable.
 * - `state`: Current lifecycle state of the timer (e.g., IDLE, RUNNING, PAUSED, FINISHED).
 *
 * **Usage:**
 * This entity tracks the timer’s state across app lifecycle events, ensuring accurate restoration and resumption
 * of the timer when the app is resumed.
 */
@Entity(tableName = "timers")
data class TimerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val startTimeMillis: Long,
    val remainingMillis: Long,
    val endTimeMillis: Long,
    val targetDurationMillis: Long,

    val isTimerRunning: Boolean,
    val isTimerSnoozed: Boolean,
    val snoozedTargetDurationMillis: Long,

    val state: TimerStatus = TimerStatus.IDLE
)