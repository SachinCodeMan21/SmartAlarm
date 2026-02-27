package com.example.smartalarm.feature.alarm.domain.repository

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining the contract for managing alarms and their associated missions.
 *
 * Provides methods to observe, retrieve, create, update, and delete alarms.
 * Implementations of this interface handle the underlying data source (e.g., database, network).
 */
interface AlarmRepository {

    /**
     * Returns a stream of alarms as a [Flow] that emits the current list of [AlarmModel]s
     * and updates whenever the list changes.
     *
     * @return A [Flow] emitting the list of all alarms.
     */
    fun getAlarms(): Flow<List<AlarmModel>>

    /**
     * Retrieves a specific [AlarmModel] by its unique identifier.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return A [MyResult] containing the [AlarmModel] if found, or an error if not.
     */
    suspend fun getAlarmById(alarmId: Int): MyResult<AlarmModel, DataError>

    /**
     * Saves a **new alarm** with its associated missions.
     *
     * @param alarm The new [AlarmModel] to save (must have ID = 0).
     * @return A [MyResult] containing the newly inserted alarm ID, or an error if failed.
     */
    suspend fun saveAlarm(alarm: AlarmModel): MyResult<Int, DataError>


    /**
     * Updates an **existing alarm** and its missions in a single transaction.
     *
     * @param alarm The [AlarmModel] to update (must have a valid non-zero ID).
     * @return A [MyResult] indicating success or failure.
     */
    suspend fun updateAlarm(alarm: AlarmModel): MyResult<Unit, DataError>


    /**
     * Deletes an alarm by its ID. Associated missions are deleted via CASCADE.
     *
     * @param alarmId The ID of the alarm to delete.
     * @return A [MyResult] indicating success or failure.
     */
    suspend fun deleteAlarmById(alarmId: Int): MyResult<Unit, DataError>
}
