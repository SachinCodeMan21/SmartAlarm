package com.example.smartalarm.feature.alarm.data.datasource.impl

import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.data.local.dao.AlarmDao
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [AlarmLocalDataSource] that interacts with the local database via [AlarmDao].
 *
 * Handles all alarm-related database operations such as fetching, saving, updating, and deleting
 * alarms and their associated missions.
 */
class AlarmLocalDataSourceImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmLocalDataSource
{

    /**
     * Fetches all alarms along with their associated missions as a flow.
     */
    override fun getAllAlarms(): Flow<List<AlarmWithMissions>> {
        return alarmDao.getAlarms()
    }

    /**
     * Retrieves a single alarm with its missions by its ID.
     *
     * @param alarmId The unique ID of the alarm.
     * @return The alarm with its missions, or null if not found.
     */
    override suspend fun getAlarmById(alarmId: Int): AlarmWithMissions? {
        return alarmDao.getAlarmById(alarmId)
    }

    /**
     * Inserts a new alarm and its missions into the database in a single transaction.
     *
     * @param alarm The alarm entity (must have ID = 0).
     * @param missions List of missions to associate with the alarm.
     * @return The generated ID of the inserted alarm.
     */
    override suspend fun saveAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>): Int {
        return alarmDao.saveAlarmWithMissions(alarm, missions)
    }

    /**
     * Updates an existing alarm and its associated missions in a single transaction.
     *
     * @param alarm The alarm to update (must have a valid ID).
     * @param missions The new list of missions to associate.
     */
    override suspend fun updateAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>) {
        alarmDao.updateAlarmWithMissions(alarm, missions)
    }

    /**
     * Inserts or updates a single alarm.
     *
     * @param alarm The alarm entity to insert or update.
     * @return The row ID of the inserted/updated alarm.
     */
    override suspend fun insertAlarm(alarm: AlarmEntity): Long {
        return alarmDao.insertAlarm(alarm)
    }

    /**
     * Inserts or updates a list of missions.
     *
     * @param missions The list of mission entities to insert or update.
     */
    override suspend fun insertMissions(missions: List<MissionEntity>) {
        alarmDao.insertMissions(missions)
    }

    /**
     * Deletes all missions associated with a given alarm ID.
     *
     * @param alarmId The ID of the alarm whose missions should be deleted.
     */
    override suspend fun deleteMissionsByAlarmId(alarmId: Int) {
        alarmDao.deleteMissionsByAlarmId(alarmId)
    }

    /**
     * Deletes an alarm by its ID. Missions are also deleted via foreign key CASCADE.
     *
     * @param alarmId The ID of the alarm to delete.
     */
    override suspend fun deleteAlarmById(alarmId: Int) {
        alarmDao.deleteAlarmById(alarmId)
    }
}

