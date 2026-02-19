package com.example.smartalarm.feature.timer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing CRUD operations on the `timer_table`.
 *
 * This DAO provides an abstraction for interacting with the timer data stored in the Room database.
 * It includes methods to retrieve, insert, update, and delete timer records.
 *
 * ## Responsibilities:
 *
 * 1. **Get All Timers:**
 *    - Fetches a reactive [Flow] stream of all timer records stored in the database.
 *    - This is useful for observing changes to the timer list in real-time.
 *
 * 2. **Save Timer (Insert or Update):**
 *    - Inserts a new timer if it doesn't exist or updates the existing one if it does.
 *    - Uses Room's `@Upsert` annotation to handle both insert and update operations in a single method.
 *
 * 3. **Delete Timer by ID:**
 *    - Deletes a timer record based on its unique `timerId`.
 *    - This operation removes the timer from the database, and it is identified using the `timerId`.
 *
 */
@Dao
interface TimerDao {

    @Query("SELECT * FROM timer_table")
    fun getTimerList(): Flow<List<TimerEntity>>

    @Upsert
    suspend fun saveTimer(timerEntity: TimerEntity)

    @Query("DELETE FROM timer_table WHERE timerId = :timerId")
    suspend fun deleteTimerById(timerId: Int)

}
