package com.example.smartalarm.feature.alarm.data.datasource.contract

import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for accessing and managing alarms and their missions.
 *
 * Provides an abstraction layer over the Room database to:
 * - Fetch alarms and their related missions.
 * - Save or update alarms with missions in a transactional manner.
 * - Perform CRUD operations on individual alarms and missions.
 */
interface AlarmLocalDataSource {

    /** Fetch all alarms along with their associated missions. */
    fun getAllAlarms(): Flow<List<AlarmWithMissions>>

    /** Fetch a single alarm by its ID, including its missions. */
    suspend fun getAlarmById(alarmId: Int): AlarmWithMissions?

    /** Insert a new alarm with missions (alarm ID must be 0). */
    suspend fun saveAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>) : Int

    /** Update an existing alarm and its missions (used for full overwrite). */
    suspend fun updateAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>)

    /** Insert or update a single alarm. Returns the inserted alarm's ID. */
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    /** Insert or update a list of missions. */
    suspend fun insertMissions(missions: List<MissionEntity>)

    /** Delete all missions linked to the given alarm ID. */
    suspend fun deleteMissionsByAlarmId(alarmId: Int)

    /** Delete a single alarm by its ID (missions deleted via CASCADE). */
    suspend fun deleteAlarmById(alarmId: Int)
}
