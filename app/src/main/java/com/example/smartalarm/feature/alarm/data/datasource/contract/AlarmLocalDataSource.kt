package com.example.smartalarm.feature.alarm.data.datasource.contract

import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import kotlinx.coroutines.flow.Flow


/**
 * Defines the contract for a local data source that manages alarms and their associated missions.
 *
 * Implementations should provide access to alarms and missions stored in a local database,
 * including observation, creation, updating, and deletion of alarms and their related missions.
 *
 * All operations that modify both alarms and missions should be executed atomically to ensure data consistency.
 */
interface AlarmLocalDataSource {

    /**
     * Observes all alarms along with their associated missions.
     *
     * The returned [Flow] emits the latest list of alarms whenever the database is updated.
     *
     * @return A [Flow] emitting a list of [AlarmWithMissions].
     */
    fun observeAllAlarms(): Flow<List<AlarmWithMissions>>

    /**
     * Retrieves a specific alarm along with its associated missions by ID.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return The [AlarmWithMissions] object if found, or `null` if no alarm exists with the given ID.
     */
    suspend fun getAlarmWithMissions(alarmId: Int): AlarmWithMissions?

    /**
     * Creates a new alarm along with its associated missions in a single transaction.
     *
     * Implementations must ensure that missions are correctly linked to the newly created alarm.
     *
     * @param alarm The [AlarmEntity] to insert. Must have `id = 0`.
     * @param missions List of [MissionEntity] to associate with the alarm.
     * @return The generated ID of the new alarm.
     * @throws IllegalArgumentException if [AlarmModel.id] is not 0.
     */
    suspend fun createAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>): Int

    /**
     * Updates an existing alarm and its associated missions in a single transaction.
     *
     * Implementations should replace all existing missions for the alarm with the provided list.
     *
     * @param alarm The [AlarmEntity] to update. Must have a valid non-zero ID.
     * @param missions List of updated [MissionEntity]s to associate with the alarm.
     * @throws IllegalArgumentException if [AlarmModel.id] is 0.
     */
    suspend fun updateAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>)

    /**
     * Deletes an alarm by its ID.
     *
     * Implementations must ensure that all associated missions are also removed, either
     * automatically (via database foreign key cascade) or explicitly.
     *
     * @param alarmId The ID of the alarm to delete.
     */
    suspend fun deleteAlarm(alarmId: Int)
}