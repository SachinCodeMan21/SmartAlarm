package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAllTimersUseCaseImpl @Inject constructor(
    private val repository: TimerRepository
) : GetAllTimersUseCase {

    /**
     * Retrieves all timers as a [StateFlow].
     *
     * @return A reactive [StateFlow] of [TimerModel] list.
     */
    override fun invoke(): StateFlow<List<TimerModel>> {
        // Returning the timer list state from the repository
        return repository.timerListState
    }
}

