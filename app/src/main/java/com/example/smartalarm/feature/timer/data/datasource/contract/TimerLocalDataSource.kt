package com.example.smartalarm.feature.timer.data.datasource.contract

import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for local timer data operations.
 *
 * This interface defines the contract for data access methods interacting with the Room database.
 * Acts as a boundary between the data source and repository layers.
 */
interface TimerLocalDataSource {

    /**
     * Returns a stream of all timers stored in the local database.
     *
     * @return A [Flow] emitting lists of [TimerEntity] as the database changes.
     */
    fun getTimerList(): Flow<List<TimerEntity>>

    /**
     * Saves a new timer into the local database.
     *
     * @param timerEntity The [TimerEntity] to insert.
     */
    suspend fun saveTimer(timerEntity: TimerEntity)

    /**
     * Deletes a timer by its ID from the local database.
     *
     * @param timerId The unique ID of the timer to delete.
     */
    suspend fun deleteTimerById(timerId: Int)
}
