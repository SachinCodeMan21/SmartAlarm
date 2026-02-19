package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAllAlarmsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Implementation of [GetAllAlarmsUseCase] that retrieves alarms from the [AlarmRepository].
 *
 * This class encapsulates the business logic for streaming all alarms.
 *
 * @property alarmRepository The repository used to access alarm data.
 */
class GetAllAlarmsUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : GetAllAlarmsUseCase {

    /**
     * Invokes the use case to retrieve a stream of all alarms.
     *
     * @return A [Flow] emitting updated lists of [AlarmModel] from the repository.
     */
    override fun invoke(): Flow<List<AlarmModel>> {
        return alarmRepository.getAlarms()
    }

}
