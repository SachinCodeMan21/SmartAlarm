package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.DeleteStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetCurrentStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.LapStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.PauseStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.StartStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.UpdateStopwatchTickerStateUseCase
import javax.inject.Inject

data class StopwatchUseCases @Inject constructor(
    val getStopwatch: GetStopwatchStateUseCase,
    val getCurrentStopwatch: GetCurrentStopwatchStateUseCase,
    val startStopwatch: StartStopwatchUseCase,
    val pauseStopwatch: PauseStopwatchUseCase,
    val deleteStopwatch: DeleteStopwatchUseCase,
    val lapStopwatch: LapStopwatchUseCase,
    val updateStopwatchTicker: UpdateStopwatchTickerStateUseCase
)

