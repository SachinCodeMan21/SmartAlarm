package com.example.smartalarm.feature.clock.framework.jobmanager.contract

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import kotlinx.coroutines.CoroutineScope

interface ClockUpdaterJob {

    fun startClockUpdaterJob(
        scope: CoroutineScope,
        savedPlaces: List<PlaceModel>,
        onUpdate: (List<PlaceModel>, String, String) -> Unit,
        onError: ((Throwable) -> Unit)
    )

    fun stopClockUpdaterJob()

}