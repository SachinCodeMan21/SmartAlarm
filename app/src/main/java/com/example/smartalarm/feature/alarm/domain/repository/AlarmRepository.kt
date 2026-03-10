package com.example.smartalarm.feature.alarm.domain.repository

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing alarms and their associated missions.
 *
 * Provides methods to observe, retrieve, create, update, and delete alarms in a consistent
 * and type-safe manner. All operations return a [MyResult] to handle success or failure
 * using [DataError].
 */
interface AlarmRepository {

    /**
     * Observes all alarms along with their associated missions.
     *
     * The returned [Flow] emits updates whenever the database changes.
     *
     * @return A [Flow] emitting a list of [AlarmModel].
     */
    fun observeAlarms(): Flow<List<AlarmModel>>

    /**
     * Retrieves a specific alarm and its missions by ID.
     *
     * If the alarm does not exist, the result will contain `null`.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return [MyResult] containing either the [AlarmModel] if found, `null` if not, or a [DataError] on failure.
     */
    suspend fun getAlarmWithMissions(alarmId: Int): MyResult<AlarmModel?, DataError>

    /**
     * Creates a new alarm along with its associated missions in a single transaction.
     *
     * @param alarm The [AlarmModel] to create.
     * @return [MyResult] containing the generated alarm ID on success, or [DataError] on failure.
     */
    suspend fun createAlarmWithMissions(alarm: AlarmModel): MyResult<Int, DataError>

    /**
     * Updates an existing alarm and its associated missions in a single transaction.
     *
     * Existing missions are replaced by the new list provided in [alarm].
     *
     * @param alarm The [AlarmModel] to update.
     * @return [MyResult] with `Unit` on success, or [DataError] on failure.
     */
    suspend fun updateAlarmWithMissions(alarm: AlarmModel): MyResult<Unit, DataError>

    /**
     * Deletes an alarm and all its associated missions (cascade) from the database.
     *
     * @param alarmId The ID of the alarm to delete.
     * @return [MyResult] with `Unit` on success, or [DataError] on failure.
     */
    suspend fun deleteAlarm(alarmId: Int): MyResult<Unit, DataError>
}