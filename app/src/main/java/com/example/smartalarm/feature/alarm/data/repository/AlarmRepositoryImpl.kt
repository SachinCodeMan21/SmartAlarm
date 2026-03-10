package com.example.smartalarm.feature.alarm.data.repository

import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.mapper.AlarmMapper
import com.example.smartalarm.feature.alarm.data.mapper.MissionMapper
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.alarm.data.mapper.AlarmMapper.toDomainModel
import com.example.smartalarm.feature.alarm.data.mapper.AlarmMapper.toEntityWithMissions
import javax.inject.Inject


/**
 * Concrete implementation of [AlarmRepository] that interacts with the local database
 * through [AlarmLocalDataSource] to manage alarms and their associated missions.
 *
 * All operations are executed safely within [myRunCatchingResult], converting exceptions
 * into [MyResult.Error] with [DataError] for consistent error handling.
 *
 * This class handles mapping between database entities ([AlarmEntity], [MissionEntity])
 * and domain models ([AlarmModel]) via [AlarmMapper] and [MissionMapper].
 *
 * @property alarmLocalDataSource The local data source used to access alarms and missions.
 */
class AlarmRepositoryImpl @Inject constructor(
    private val alarmLocalDataSource: AlarmLocalDataSource
) : AlarmRepository {

    /**
     * Observes all alarms along with their associated missions.
     *
     * Returns a [Flow] that emits the latest list of [AlarmModel] whenever the database changes.
     *
     * @return A [Flow] emitting a list of [AlarmModel].
     */
    override fun observeAlarms(): Flow<List<AlarmModel>> {
        return alarmLocalDataSource.observeAllAlarms().map { alarmWithMissionList ->
            alarmWithMissionList.map { it.toDomainModel() }
        }
    }

    /**
     * Retrieves a specific alarm and its missions by ID.
     *
     * If the alarm is not found, the result contains `null`.
     * Any unexpected errors (e.g., database issues) are wrapped in [MyResult.Error].
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return [MyResult] containing either the [AlarmModel] if found, `null` if not, or a [DataError].
     */
    override suspend fun getAlarmWithMissions(alarmId: Int): MyResult<AlarmModel?, DataError> {
        return myRunCatchingResult {
            val alarmWithMissions = alarmLocalDataSource.getAlarmWithMissions(alarmId)
            alarmWithMissions?.toDomainModel()
        }
    }

    /**
     * Creates a new alarm along with its associated missions in a single transaction.
     *
     * @param alarm The [AlarmModel] to create.
     * @return [MyResult] containing the generated alarm ID on success, or [DataError] on failure.
     */
    override suspend fun createAlarmWithMissions(alarm: AlarmModel): MyResult<Int, DataError> {
        return myRunCatchingResult {
            val (alarmEntity, missionEntities) = alarm.toEntityWithMissions()
            alarmLocalDataSource.createAlarmWithMissions(alarmEntity, missionEntities)
        }
    }

    /**
     * Updates an existing alarm along with its associated missions in a single transaction.
     *
     * Existing missions are replaced with the new list from [alarm].
     *
     * @param alarm The [AlarmModel] to update.
     * @return [MyResult] with `Unit` on success, or [DataError] on failure.
     */
    override suspend fun updateAlarmWithMissions(alarm: AlarmModel): MyResult<Unit, DataError> {
        return myRunCatchingResult {
            val (alarmEntity, missionEntities) = alarm.toEntityWithMissions()
            alarmLocalDataSource.updateAlarmWithMissions(alarmEntity, missionEntities)
        }
    }

    /**
     * Deletes an alarm and all its associated missions (cascade) from the database.
     *
     * @param alarmId The ID of the alarm to delete.
     * @return [MyResult] with `Unit` on success, or [DataError] on failure.
     */
    override suspend fun deleteAlarm(alarmId: Int): MyResult<Unit, DataError> {
        return myRunCatchingResult {
            alarmLocalDataSource.deleteAlarm(alarmId)
        }
    }
}


