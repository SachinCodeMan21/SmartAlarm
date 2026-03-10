package com.example.smartalarm.feature.alarm.data.datasource.impl

import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.data.local.dao.AlarmDao
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import androidx.room.ForeignKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of [AlarmLocalDataSource] that uses [AlarmDao] to
 * persist, update, and query alarms along with their associated missions.
 *
 * All operations that modify alarms or missions are executed via Room,
 * leveraging transactions and foreign key constraints where appropriate.
 *
 * @property alarmDao The DAO used to access alarm and mission data in the database.
 */
class AlarmLocalDataSourceImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmLocalDataSource {

    /**
     * Returns a flow emitting all alarms along with their associated missions.
     *
     * This observes the database for changes and emits updated lists automatically.
     *
     * @return A [Flow] of [AlarmWithMissions] representing all alarms.
     */
    override fun observeAllAlarms(): Flow<List<AlarmWithMissions>> {
        return alarmDao.observeAllAlarms()
    }

    /**
     * Retrieves a specific alarm along with its associated missions by ID.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return The [AlarmWithMissions] object, or null if no alarm exists with the given ID.
     */
    override suspend fun getAlarmWithMissions(alarmId: Int): AlarmWithMissions? {
        return alarmDao.getAlarmWithMissions(alarmId)
    }

    /**
     * Creates a new alarm along with its associated missions in a single transaction.
     *
     * Existing missions are not affected since this creates a new alarm.
     *
     * @param alarm The [AlarmEntity] to insert. Must have `id = 0`.
     * @param missions List of [MissionEntity] to associate with the alarm.
     * @return The generated ID of the new alarm.
     * @throws IllegalArgumentException if [AlarmModel.id] is not 0.
     */
    override suspend fun createAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>): Int {
        return alarmDao.createAlarmWithMissions(alarm, missions)
    }

    /**
     * Updates an existing alarm and its associated missions in a single transaction.
     *
     * Existing missions for the alarm are deleted and replaced with the provided list.
     *
     * @param alarm The [AlarmEntity] to update. Must have a valid non-zero ID.
     * @param missions List of updated [MissionEntity]s to associate with the alarm.
     * @throws IllegalArgumentException if [AlarmModel.id] is 0.
     */
    override suspend fun updateAlarmWithMissions(alarm: AlarmEntity, missions: List<MissionEntity>) {
        alarmDao.updateAlarmWithMissions(alarm, missions)
    }

    /**
     * Deletes an alarm by its ID.
     *
     * All associated missions are automatically deleted due to the
     * [ForeignKey.CASCADE] constraint defined in the database schema.
     *
     * @param alarmId The ID of the alarm to delete.
     */
    override suspend fun deleteAlarm(alarmId: Int) {
        alarmDao.deleteAlarm(alarmId)
    }
}