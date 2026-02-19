package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result


/**
 * Interface defining the use case for saving an alarm.
 *
 * This use case provides a method to save an [AlarmModel] to a repository. The operation returns a
 * [Result] object, which can either be a [Result.Success] containing the saved alarm's ID or a
 * [Result.Error] in case of a failure.
 */
interface SaveAlarmUseCase {

    /**
     * Saves the provided alarm model and returns its ID on success.
     *
     * @param alarm The [AlarmModel] to be saved.
     * @return A [Result] containing the saved alarm's ID on success, or an error on failure.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<Int>
}
