package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import javax.inject.Inject


/**
 * Implementation of the [UpdateAlarmUseCase] for updating an alarm.
 *
 * This use case interacts with the [AlarmRepository] to update an existing alarm model. It returns the result
 * of the update operation. If successful, it returns a [Result.Success]; if an error occurs, it returns an
 * error message using the [ResourceProvider] for localization.
 *
 * @param alarmRepository The repository used to update the alarm details.
 * @param resourceProvider Provides localized resources, including error messages.
 */
class UpdateAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val resourceProvider: ResourceProvider
) : UpdateAlarmUseCase {

    /**
     * Updates the alarm model in the repository and returns the result.
     *
     * This method attempts to update the provided [AlarmModel]. If successful, it returns a
     * [Result.Success] with `Unit`, indicating the update was successful. If it fails, it returns
     * a [Result.Error] with a localized error message.
     *
     * @param alarm The [AlarmModel] to be updated.
     * @return A [Result] indicating the success or failure of the update operation.
     */
    override suspend fun invoke(alarm: AlarmModel): Result<Unit> {

        // Attempt to update the alarm in the repository
        val result = alarmRepository.updateAlarm(alarm)

        return when (result) {

            // If the update operation is successful, return a success result
            is Result.Success -> {
                Result.Success(Unit)
            }

            // If the update operation fails, return an error result with a localized error message
            is Result.Error -> {
                Result.Error(
                    Exception(resourceProvider.getString(R.string.failed_to_update_the_alarm_details))
                )
            }
        }
    }
}


