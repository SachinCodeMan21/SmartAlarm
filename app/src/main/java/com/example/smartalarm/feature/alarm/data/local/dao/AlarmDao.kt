package com.example.smartalarm.feature.alarm.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for performing operations on alarms and their associated missions.
 *
 * This DAO provides methods for:
 *  - Inserting, updating, and deleting alarms and missions.
 *  - Observing all alarms with their missions as a reactive Flow.
 *  - Transactionally creating or updating alarms along with their missions.
 *
 * All transactional methods ensure consistency between the [AlarmEntity] and its [MissionEntity] children.
 */
@Dao
interface AlarmDao {

    // ---------------------------------------------------------------------
    // Insert & Update Operations
    // ---------------------------------------------------------------------

    /**
     * Saves an [AlarmEntity] to the database.
     *
     * If the alarm already exists (same primary key), it will be updated.
     * Uses Room's [Upsert] behavior.
     *
     * @param alarm The alarm entity to save or update.
     * @return The row ID of the inserted/updated alarm.
     */
    @Upsert
    suspend fun saveAlarm(alarm: AlarmEntity): Long

    /**
     * Saves a list of [MissionEntity]s to the database.
     *
     * Each mission is upserted: new missions are inserted, existing missions are updated.
     *
     * @param missions List of mission entities to save or update.
     */
    @Upsert
    suspend fun saveMissions(missions: List<MissionEntity>)



    // ---------------------------------------------------------------------
    // Delete Operations
    // ---------------------------------------------------------------------

    /**
     * Deletes a single alarm by its ID.
     *
     * @param alarmId The ID of the alarm to delete.
     */
    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Int)

    /**
     * Deletes all missions associated with a specific alarm.
     *
     * @param alarmId The ID of the parent alarm whose missions should be deleted.
     */
    @Query("DELETE FROM mission_table WHERE alarmId = :alarmId")
    suspend fun deleteMissionsForAlarm(alarmId: Int)


    // ---------------------------------------------------------------------
    // Query / Observation Operations
    // ---------------------------------------------------------------------

    /**
     * Observes all alarms along with their associated missions.
     *
     * Returns a [Flow] of a list of [AlarmWithMissions] to provide reactive updates
     * whenever the alarms or missions change in the database.
     *
     * @return [Flow] emitting lists of alarms with their missions.
     */
    @Transaction
    @Query("SELECT * FROM alarm_table")
    fun observeAllAlarms(): Flow<List<AlarmWithMissions>>

    /**
     * Fetches a single alarm along with its associated missions by alarm ID.
     *
     * Returns `null` if no alarm with the given ID exists.
     *
     * @param alarmId The ID of the alarm to fetch.
     * @return The [AlarmWithMissions] object or `null` if not found.
     */
    @Transaction
    @Query("SELECT * FROM alarm_table WHERE id = :alarmId")
    suspend fun getAlarmWithMissions(alarmId: Int): AlarmWithMissions?


    // ---------------------------------------------------------------------
    // Transactional Save & Update Operations
    // ---------------------------------------------------------------------

    /**
     * Creates a new alarm with its associated missions in a single transaction.
     *
     * Ensures atomicity: the alarm and missions are inserted together.
     *
     * @param alarm The new [AlarmEntity] to create. Must have ID = 0.
     * @param missions List of [MissionEntity]s to associate with the alarm.
     * @return The generated alarm ID of the newly created alarm.
     * @throws IllegalArgumentException If [AlarmModel.id] is not 0.
     */
    @Transaction
    suspend fun createAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ): Int {

        require(alarm.id == 0) { "Alarm ID must be 0 for new alarms" }

        val alarmId = saveAlarm(alarm).toInt()

        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarmId) }
            saveMissions(updatedMissions)
        }

        return alarmId
    }

    /**
     * Updates an existing alarm along with its associated missions in a single transaction.
     *
     * This method performs a **full replacement** of the alarm’s missions:
     * 1. The alarm itself is upserted (updated in place).
     * 2. All existing missions associated with the alarm are deleted.
     * 3. The provided list of missions is inserted and linked to the alarm.
     *
     * Deleting the previous missions ensures that the alarm’s missions in the database
     * exactly match the provided list, preventing stale or orphaned mission entries.
     *
     * @param alarm The existing [AlarmEntity] to update. Must have a valid non-zero ID.
     * @param missions List of updated [MissionEntity]s to associate with the alarm.
     * @throws IllegalArgumentException If [AlarmModel.id] is 0.
     */
    @Transaction
    suspend fun updateAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ) {
        require(alarm.id != 0) { "Cannot update alarm with ID = 0" }

        saveAlarm(alarm) // Upsert to update existing alarm

        deleteMissionsForAlarm(alarm.id)

        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarm.id) }
            saveMissions(updatedMissions)
        }
    }
}