package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult


/**
 * Interface defining the use case for updating an alarm.
 *
 * This use case provides a method to update an [AlarmModel] in a repository. The operation returns a
 * [MyResult] object, which can either be a [MyResult.Success] or a [MyResult.Error] in case of a failure.
 */
interface UpdateAlarmUseCase {

    /**
     * Updates the provided alarm model in the repository.
     *
     * @param alarm The [AlarmModel] to be updated.
     * @return A [MyResult] indicating the success or failure of the update operation.
     */
    suspend operator fun invoke(alarm: AlarmModel): MyResult<Unit, DataError>
}
