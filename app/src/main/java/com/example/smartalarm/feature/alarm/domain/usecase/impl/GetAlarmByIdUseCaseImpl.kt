package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import javax.inject.Inject



/**
 * Implementation of the [GetAlarmByIdUseCase] that retrieves alarm details by its ID.
 *
 * This use case interacts with the [AlarmRepository] to fetch alarm data, handling the result
 * appropriately by returning a [Result] object. In case of a successful retrieval, it returns the
 * alarm data; if an error occurs, it provides a failure message retrieved from the [ResourceProvider].
 *
 * @param alarmRepository The repository used to fetch the alarm details from the data source.
 * @param resourceProvider Provides localized resources, including error messages.
 */
class GetAlarmByIdUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val resourceProvider: ResourceProvider
) : GetAlarmByIdUseCase {

    /**
     * Retrieves the alarm details by its ID from the repository.
     *
     * This method calls the [AlarmRepository.getAlarmById] function to fetch the alarm.
     * If successful, it returns a [Result.Success] containing the alarm details.
     * If an error occurs (e.g., network failure or missing alarm), it returns a [Result.Error]
     * with a localized error message.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return A [Result] containing either the [AlarmModel] on success or an error on failure.
     */
    override suspend fun invoke(alarmId: Int): Result<AlarmModel> {

        val result = alarmRepository.getAlarmById(alarmId)

        return when(result) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(Exception(resourceProvider.getString(R.string.failed_to_retrieve_the_alarm_details)))
            }
        }

    }
}
