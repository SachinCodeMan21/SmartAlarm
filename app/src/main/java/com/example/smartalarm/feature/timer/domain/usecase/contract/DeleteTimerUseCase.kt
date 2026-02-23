package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel


/**
 * Use case for deleting a timer from the data source by its ID.
 */
fun interface DeleteTimerUseCase {
    /**
     * Deletes the given timer.
     *
     * @param timer The [TimerModel] to delete.
     * @return A [MyResult] indicating success or failure.
     */
    suspend operator fun invoke(timer: TimerModel): MyResult<Unit, DataError>
}
