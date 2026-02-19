package com.example.smartalarm.feature.timer.framework.jobmanager.contract

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import kotlinx.coroutines.CoroutineScope

interface ShowTimerJobManager {

    fun startTimerTickerJob(
        scope: CoroutineScope,
        shouldContinue: () -> Boolean,
        onTick: suspend () -> Unit
    )

    fun stopTimerTickerJob()

}

