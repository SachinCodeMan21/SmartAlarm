package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result

/**
 * Use case interface for retrieving a specific alarm by its unique ID.
 *
 * This abstraction provides a clear separation of concerns, decoupling business logic from its
 * implementation. This allows for easier testing and enhances the modularity of the codebase.
 */
interface GetAlarmByIdUseCase {

    /**
     * Retrieves an alarm by its unique identifier.
     *
     * @param alarmId The unique identifier of the alarm to retrieve.
     * @return A [Result] containing the [AlarmModel] if retrieval is successful, or an error if the retrieval fails.
     *         The [Result] wrapper is used to represent either a successful outcome (with the [AlarmModel])
     *         or a failure (with an error).
     */
    suspend operator fun invoke(alarmId: Int): Result<AlarmModel>
}
