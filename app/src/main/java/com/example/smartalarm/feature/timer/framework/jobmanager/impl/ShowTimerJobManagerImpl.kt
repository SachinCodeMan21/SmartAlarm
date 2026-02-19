package com.example.smartalarm.feature.timer.framework.jobmanager.impl

import android.util.Log
import com.example.smartalarm.feature.timer.framework.jobmanager.contract.ShowTimerJobManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShowTimerJobManagerImpl @Inject constructor() : ShowTimerJobManager {

    private var globalTimerTickJob: Job? = null

    override fun startTimerTickerJob(
        scope: CoroutineScope,
        shouldContinue: () -> Boolean,
        onTick: suspend () -> Unit
    ) {
        if (globalTimerTickJob?.isActive == true) return

        globalTimerTickJob = scope.launch {

            while (isActive) {
                // 1. Check if we should still be running
                if (!shouldContinue()) {
                    stopTimerTickerJob()
                    break
                }

                Log.d("TAG"," Job Running ")


                // 2. Perform the tick
                onTick()

                // 3. Wait for the next second
                delay(1000)
            }
        }
    }

    override fun stopTimerTickerJob() {
        globalTimerTickJob?.cancel()
        globalTimerTickJob = null
    }
}