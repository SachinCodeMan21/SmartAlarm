package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.SaveTimerUseCase
import javax.inject.Inject

/**
 * Implementation of [SaveTimerUseCase] that persists a timer in the data source.
 *
 * Uses [TimerRepository] to save the timer entity.
 *
 * @property repository Repository used to persist timer data.
 */
class SaveTimerUseCaseImpl @Inject constructor(
    private val repository: TimerRepository
) : SaveTimerUseCase {

    /**
     * Saves or updates a timer in the repository.
     * * @param timer The timer model to persist.
     * @return [MyResult.Success] if saved, or [MyResult.Error] with [DataError] details.
     */
    override suspend fun invoke(timer: TimerModel): MyResult<Unit, DataError> {
        // We simply forward the repository result.
        // The DataError mapping happens inside the Repository implementation.
        return repository.persistTimer(timer)
    }
}