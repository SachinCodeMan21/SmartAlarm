package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.model.Result

/**
 * Use case interface for deleting an alarm by its unique ID.
 *
 * Provides an abstraction to encapsulate the logic required to remove an alarm
 * from the underlying data source.
 */
interface DeleteAlarmUseCase {

    /**
     * Deletes an alarm with the specified ID.
     *
     * @param alarmId The unique identifier of the alarm to be deleted.
     * @return A [Result] indicating whether the deletion was successful or if an error occurred.
     */
    suspend operator fun invoke(alarmId: Int): Result<Unit>
}
