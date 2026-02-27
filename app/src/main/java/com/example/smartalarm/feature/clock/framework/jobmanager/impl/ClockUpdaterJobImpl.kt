package com.example.smartalarm.feature.clock.framework.jobmanager.impl

import com.example.smartalarm.core.framework.di.annotations.IoDispatcher
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import com.example.smartalarm.core.utility.extension.toClockTimeFormat
import com.example.smartalarm.core.utility.extension.toDayMonthFormat
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.clock.domain.usecase.contract.UpdateClockUseCase
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ClockUpdaterJobImpl @Inject constructor(
    private val clockProvider: SystemClockHelper,
    private val updateClockUseCase: UpdateClockUseCase,
    @param:IoDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ClockUpdaterJob {

    private var updateJob: Job? = null

    override fun startClockUpdaterJob(
        scope: CoroutineScope,
        savedPlaces: List<PlaceModel>,
        onUpdate: (List<PlaceModel>, String, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        stopClockUpdaterJob() // Cancel any previous job
        updateJob = scope.launch(defaultDispatcher) {
            while (isActive) {
                val now = clockProvider.getCurrentTime()
                val formattedTime = now.toClockTimeFormat()
                val formattedDate = now.toDayMonthFormat()

                val updatedPlaces = updateClockUseCase(savedPlaces)
                onUpdate(updatedPlaces, formattedTime, formattedDate)

                val delayMillis = TimeUnit.MINUTES.toMillis(1) - (now % TimeUnit.MINUTES.toMillis(1))
                delay(delayMillis)
            }
        }
    }

    override fun stopClockUpdaterJob() {
        updateJob?.cancel()
        updateJob = null
    }

}

