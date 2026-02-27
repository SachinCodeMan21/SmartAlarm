package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import javax.inject.Inject


/**
 * Implementation of the [SaveAlarmUseCase] for saving an alarm.
 *
 * This use case interacts with the [AlarmRepository] to save an alarm model. It returns the result of
 * the save operation. If successful, it returns the saved alarm's ID; if an error occurs, it returns
 * a failure message using the [ResourceProvider] for localization.
 *
 * @param alarmRepository The repository used to save the alarm details.
 * @param resourceProvider Provides localized resources, including error messages.
 */
class SaveAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val resourceProvider: ResourceProvider
) : SaveAlarmUseCase {

    /**
     * Saves the alarm model to the repository and returns the result.
     *
     * The method attempts to save the provided [AlarmModel] and returns a result indicating success
     * (with the alarm's ID) or failure (with a localized error message).
     *
     * @param alarm The [AlarmModel] to be saved.
     * @return A [MyResult] containing the saved alarm's ID if successful, or an error if the operation fails.
     */
    override suspend fun invoke(alarm: AlarmModel): MyResult<Int, DataError> {
        return alarmRepository.saveAlarm(alarm)
    }
}


