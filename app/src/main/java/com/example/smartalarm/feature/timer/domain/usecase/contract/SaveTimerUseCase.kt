package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.core.model.Result


/**
 * Use case for saving a timer to a data source.
 * Persists the given timer entity.
 */
fun interface SaveTimerUseCase {

    /**
     * Saves the given timer to the data source.
     *
     * @param timer The [TimerModel] to save.
     * @return A [Result] indicating success or failure of the save operation.
     */
    suspend operator fun invoke(timer: TimerModel): Result<Unit>

}