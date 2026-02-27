package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult

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
     * @return A [MyResult] indicating whether the deletion was successful or if an error occurred.
     */
    suspend operator fun invoke(alarmId: Int): MyResult<Unit, DataError>
}
