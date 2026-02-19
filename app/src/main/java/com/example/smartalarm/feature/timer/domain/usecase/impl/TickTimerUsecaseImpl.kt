package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.TickTimerUsecase
import javax.inject.Inject

class TickTimerUsecaseImpl @Inject constructor(
    private val repository: TimerRepository
) : TickTimerUsecase{
    override fun invoke() {
        repository.tickAllRunningTimers()
    }

}