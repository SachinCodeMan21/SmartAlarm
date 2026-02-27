package com.example.smartalarm.feature.alarm.data.repository

import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.alarm.data.mapper.AlarmMapper.toDomainModel
import com.example.smartalarm.feature.alarm.data.mapper.AlarmMapper.toEntityWithMissions
import javax.inject.Inject


/**
 * Repository interface for managing alarms and their associated missions.
 *
 * Provides methods to observe, retrieve, create, update, and delete alarms.
 * All operations wrap results in [MyResult] for safe error handling.
 *
 * The repository exposes alarms as domain models ([AlarmModel]) and handles
 * the conversion to/from persistence entities internally.
 */
class AlarmRepositoryImpl @Inject constructor(
    private val alarmLocalDataSource: AlarmLocalDataSource
) : AlarmRepository {

    /**
     * Returns a stream of alarms as a [Flow] that emits the current list of [AlarmModel]s
     * and updates whenever the list changes.
     *
     * @return A [Flow] emitting the list of all alarms.
     */
    override fun getAlarms(): Flow<List<AlarmModel>> {
        return alarmLocalDataSource.getAllAlarms().map { alarmWithMissionList ->
            alarmWithMissionList.map { it.toDomainModel() }
        }
    }

    /**
     * Retrieves a specific [AlarmModel] by its unique identifier.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return A [MyResult] containing the [AlarmModel] if found, or an error if not.
     */
    override suspend fun getAlarmById(alarmId: Int): MyResult<AlarmModel, DataError> {
        return myRunCatchingResult {
            val alarmWithMissions = alarmLocalDataSource.getAlarmById(alarmId)
            alarmWithMissions?.toDomainModel()
                ?: throw NoSuchElementException("Alarm with id $alarmId not found")
        }
    }

    /**
     * Saves a new alarm along with its associated missions.
     *
     * @param alarm The [AlarmModel] to save (must have an ID of 0).
     * @return A [MyResult] containing the newly generated alarm ID on success, or an error.
     */
    override suspend fun saveAlarm(alarm: AlarmModel): MyResult<Int, DataError> {
        return myRunCatchingResult {
            val (alarmEntity, missionEntities) = alarm.toEntityWithMissions()
            alarmLocalDataSource.saveAlarmWithMissions(alarmEntity, missionEntities)
        }
    }

    /**
     * Updates an existing alarm and its missions.
     *
     * @param alarm The [AlarmModel] with updated data (must have a valid non-zero ID).
     * @return A [MyResult] indicating success or failure.
     */
    override suspend fun updateAlarm(alarm: AlarmModel): MyResult<Unit, DataError> {
        return myRunCatchingResult {
            val (alarmEntity, missionEntities) = alarm.toEntityWithMissions()
            alarmLocalDataSource.updateAlarmWithMissions(alarmEntity, missionEntities)
        }
    }

    /**
     * Deletes an alarm by its unique identifier.
     *
     * @param alarmId The ID of the alarm to delete.
     * @return A [MyResult] indicating success or failure.
     */
    override suspend fun deleteAlarmById(alarmId: Int): MyResult<Unit, DataError> {
        return myRunCatchingResult {
            alarmLocalDataSource.deleteAlarmById(alarmId)
        }
    }

}


