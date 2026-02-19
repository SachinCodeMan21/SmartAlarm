package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.SaveTimerUseCase
import com.example.smartalarm.core.model.Result
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
     * Saves the timer to the repository.
     *
     * @param timer The timer to save.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun invoke(timer: TimerModel): Result<Unit> {
        return repository.persistTimer(timer)
    }
}
