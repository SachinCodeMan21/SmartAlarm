package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import javax.inject.Inject


/**
 * Implementation of [DeleteAlarmUseCase] that performs the deletion operation
 * by delegating it to the [AlarmRepository].
 *
 * This class is part of the domain layer and encapsulates the deletion logic.
 *
 * @property alarmRepository The repository responsible for managing alarm data.
 */
class DeleteAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : DeleteAlarmUseCase {

    /**
     * Invokes the use case to delete an alarm by its ID.
     *
     * @param alarmId The ID of the alarm to delete.
     * @return A [MyResult] representing the outcome of the delete operation — success or failure.
     */
    override suspend fun invoke(alarmId: Int): MyResult<Unit, DataError> {
        return alarmRepository.deleteAlarmById(alarmId)
    }
}
