package com.example.testutils.sharedFake

import com.example.smartalarm.feature.alarm.data.local.dao.AlarmDao
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of the [AlarmDao] interface for unit testing.
 *
 * This class mimics the behavior of the Room database by storing alarms and missions in memory using
 * mutable lists. It is used for testing the business logic in the data source without relying on
 * an actual database.
 */
class FakeAlarmDao : AlarmDao {

    // In-memory storage for alarms and associated missions
    private val alarms = mutableListOf<AlarmEntity>()
    private val missions = mutableListOf<MissionEntity>()

    /**
     * Simulates inserting an alarm into the database.
     *
     * This method assigns an auto-incrementing ID to the alarm and adds it to the in-memory list.
     *
     * @param alarm The alarm entity to insert.
     * @return The generated ID of the inserted alarm.
     */
    override suspend fun insertAlarm(alarm: AlarmEntity): Long {
        val newId = alarms.size + 1L // Auto-incrementing ID
        alarms.add(alarm.copy(id = newId.toInt()))  // Adding the alarm with the new ID
        return newId
    }

    /**
     * Simulates retrieving an alarm by its ID along with its associated missions.
     *
     * Searches the in-memory list for the alarm with the given ID and fetches its associated missions.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return An [AlarmWithMissions] object if the alarm is found, or null if not.
     */
    override suspend fun getAlarmById(alarmId: Int): AlarmWithMissions? {
        val alarm = alarms.find { it.id == alarmId } // Find alarm by ID
        val associatedMissions = missions.filter { it.alarmId == alarmId } // Get associated missions
        return if (alarm != null) AlarmWithMissions(alarm, associatedMissions) else null
    }

    /**
     * Simulates inserting a list of missions into the database.
     *
     * Adds the given missions to the in-memory list.
     *
     * @param missions A list of [MissionEntity] to insert.
     */
    override suspend fun insertMissions(missions: List<MissionEntity>) {
        this.missions.addAll(missions) // Add missions to the list
    }

    /**
     * Simulates deleting all missions associated with a specific alarm ID.
     *
     * Removes missions from the in-memory list where the [alarmId] matches.
     *
     * @param alarmId The ID of the alarm whose associated missions should be deleted.
     */
    override suspend fun deleteMissionsByAlarmId(alarmId: Int) {
        missions.removeIf { it.alarmId == alarmId }  // Remove missions related to the given alarm ID
    }

    /**
     * Simulates deleting an alarm and its associated missions from the database.
     *
     * Removes the alarm with the specified ID from the in-memory list, as well as any associated missions.
     *
     * @param alarmId The ID of the alarm to delete.
     */
    override suspend fun deleteAlarmById(alarmId: Int) {
        alarms.removeIf { it.id == alarmId }  // Remove the alarm from the list
        missions.removeIf { it.alarmId == alarmId }  // Remove associated missions
    }

    /**
     * Simulates fetching all alarms with their associated missions.
     *
     * This method returns a flow of [AlarmWithMissions], mapping over all alarms and fetching their
     * associated missions from the in-memory list.
     *
     * @return A [Flow] of [AlarmWithMissions] containing all alarms and their associated missions.
     */
    override fun getAlarms(): Flow<List<AlarmWithMissions>> {
        return flow {
            val alarmWithMissionsList = alarms.map { alarm ->
                val associatedMissions = missions.filter { it.alarmId == alarm.id }  // Fetch associated missions
                AlarmWithMissions(alarm, associatedMissions)
            }
            emit(alarmWithMissionsList)  // Emit the list of alarms with missions
        }
    }

    /**
     * Simulates saving a new alarm along with its associated missions in a single transaction.
     *
     * Inserts the alarm (with ID set to 0) and associates the given missions with the alarm.
     *
     * @param alarm The new [AlarmEntity] to insert (ID must be 0).
     * @param missions The list of [MissionEntity] to associate with the alarm.
     * @return The ID of the newly inserted alarm.
     */
    override suspend fun saveAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ): Int {
        require(alarm.id == 0) { "Alarm ID must be 0 for new alarms" }

        val alarmId = insertAlarm(alarm).toInt() // Insert the alarm and get its ID
        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarmId) } // Associate missions with the alarm ID
            insertMissions(updatedMissions)
        }
        return alarmId
    }

    /**
     * Simulates updating an existing alarm and its associated missions in a single transaction.
     *
     * Replaces the alarm and its associated missions with the new data. If the alarm already exists,
     * it will be updated; otherwise, a new alarm will be inserted.
     *
     * @param alarm The alarm to update (must have a valid ID).
     * @param missions The new list of missions to associate with the alarm.
     */
    override suspend fun updateAlarmWithMissions(
        alarm: AlarmEntity,
        missions: List<MissionEntity>
    ) {
        require(alarm.id != 0) { "Cannot update alarm with ID = 0" }

        // Update the alarm if it exists, otherwise insert a new alarm
        val index = alarms.indexOfFirst { it.id == alarm.id }
        if (index >= 0) {
            alarms[index] = alarm  // Update the existing alarm
        } else {
            insertAlarm(alarm)  // Insert a new alarm if not found
        }

        // Delete old missions and insert the updated ones
        deleteMissionsByAlarmId(alarm.id)
        if (missions.isNotEmpty()) {
            val updatedMissions = missions.map { it.copy(alarmId = alarm.id) }  // Associate missions with the alarm ID
            insertMissions(updatedMissions)
        }
    }
}
