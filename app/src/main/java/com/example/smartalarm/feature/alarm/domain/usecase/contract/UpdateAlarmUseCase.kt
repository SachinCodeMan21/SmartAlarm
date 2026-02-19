package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result


/**
 * Interface defining the use case for updating an alarm.
 *
 * This use case provides a method to update an [AlarmModel] in a repository. The operation returns a
 * [Result] object, which can either be a [Result.Success] or a [Result.Error] in case of a failure.
 */
interface UpdateAlarmUseCase {

    /**
     * Updates the provided alarm model in the repository.
     *
     * @param alarm The [AlarmModel] to be updated.
     * @return A [Result] indicating the success or failure of the update operation.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<Unit>
}
