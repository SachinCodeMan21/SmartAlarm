package com.example.smartalarm.feature.timer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Timer feature.
 * * **Why this exists:**
 * This interface serves as the mediation layer between the app's business logic and
 * the SQLite database. It ensures that timer states (Running, Paused, Snoozed)
 * survive beyond the lifecycle of the UI or a background service.
 *
 * **Key Responsibilities:**
 * 1. **Persistence:** Saving current timer progress to prevent data loss on process death.
 * 2. **Reactivity:** Providing [Flow]-based streams so the UI can stay synchronized
 * with the database state in real-time.
 * 3. **Cleanup:** Managing the removal of expired or dismissed timer sessions.
 *
 * **Threading:**
 * All functions (except those returning [Flow]) are marked as `suspend` and are
 * optimized by Room to run on a background thread pool.
 */
@Dao
interface TimerDao {

    /**
     * Provides a reactive stream of all saved timers.
     * * **Why:** This allows the UI to automatically refresh whenever a timer
     * is added, removed, or updated without manual polling. It serves as
     * the Single Source of Truth for the timer list screen.
     */
    @Query("SELECT * FROM timers")
    fun getTimerList(): Flow<List<TimerEntity>>

    /**
     * Persists or updates a timer session.
     * * **Why:** We use Upsert to handle both the initial creation and
     * subsequent state updates (like pausing or snoozing) through a
     * single entry point. This ensures that the disk state stays in
     * sync with the in-memory countdown.
     */
    @Upsert
    suspend fun persistTimer(timerEntity: TimerEntity)

    /**
     * Removes a specific timer from the database.
     * * **Why:** This is used when a user dismisses a finished timer or
     * manually deletes one from the list. Removing the record stops
     * it from being restored after process death or a device reboot.
     * * @param timerId The unique primary key of the timer to be purged.
     */
    @Query("DELETE FROM timers WHERE id = :timerId")
    suspend fun deleteTimerById(timerId: Int)
}