package com.example.smartalarm.feature.alarm.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing alarms and their associated missions.
 *
 * This interface provides methods for:
 * - Inserting, updating, and deleting alarms and missions.
 * - Fetching alarms along with their related missions using Room's @Transaction.
 * - Saving or updating an alarm and its missions atomically to maintain consistency.
 *
 * Relationships:
 * - Each alarm can have multiple missions.
 * - Missions are linked to alarms via a foreign key (`alarmId`) and use `ON DELETE CASCADE`.
 *
 * Usage:
 * - Use [saveAlarmWithMissions] when creating a **new** alarm (ID must be 0).
 * - Use [updateAlarmWithMissions] when updating an **existing** alarm (ID must be non-zero).
 */
@Dao
interface AlarmDao {



    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------


    // Simple Insert & Update Operations

    /** Insert or update a single alarm */
    @Upsert
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    /** Insert or update multiple missions */
    @Upsert
    suspend fun insertMissions(missions: List<MissionEntity>)



    // Delete Operations

    /** Delete alarm by ID (missions will be deleted via CASCADE) */
    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    /** Delete all missions for an alarm */
    @Query("DELETE FROM mission_table WHERE alarmId = :alarmId")
    suspend fun deleteMissionsByAlarmId(alarmId: Int)





    // ---------------------------------------------------------------------
    // Transactional Ops
    // ---------------------------------------------------------------------

    // Getters

    /** Fetch all alarms with their missions */
    @Transaction
    @Query("SELECT * FROM alarm_table")
    fun getAlarms(): Flow<List<AlarmWithMissions>>

    /** Fetch one alarm with its missions */
    @Transaction
    @Query("SELECT * FROM alarm_table WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Int): AlarmWithMissions?


    // Save & Update
    /**
     * Saves a new alarm along with its associated missions in a single transaction.
     *
     * Ensures the alarm is inserted first to obtain a valid ID, then inserts the missions
     * linked to that ID. This method should only be used for creating new alarms.
     *
     * @param alarm The new alarm to insert (ID must be 0).
     * @param missions List of missions to associate with the alarm.
     * @return The newly generated alarm ID.
     *
     * @throws IllegalArgumentException if the alarm ID is not 0.
     */
    @Transaction
    suspend fun saveAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ) : Int {

        require(alarm.id == 0) { "Alarm ID must be 0 for new alarms" }

        val alarmId = insertAlarm(alarm).toInt()

        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarmId) }
            insertMissions(updatedMissions)
        }

        return alarmId
    }


    /**
     * Updates an existing alarm and its missions in a single transaction.
     *
     * Replaces the alarm and its associated missions with the new data.
     * Alarm must already exist (i.e., have a non-zero ID).
     *
     * @param alarm The alarm to update (must have a valid ID).
     * @param missions New list of missions to associate with the alarm.
     *
     * @throws IllegalArgumentException if the alarm ID is 0.
     */
    @Transaction
    suspend fun updateAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ) {
        require(alarm.id != 0) { "Cannot update alarm with ID = 0" }

        insertAlarm(alarm) // Upsert

        deleteMissionsByAlarmId(alarm.id)

        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarm.id) }
            insertMissions(updatedMissions)
        }

    }

}
