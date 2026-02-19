package com.example.smartalarm.feature.clock.framework.jobmanager.impl

import com.example.smartalarm.core.di.annotations.IoDispatcher
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import com.example.smartalarm.core.utility.extension.toClockTimeFormat
import com.example.smartalarm.core.utility.extension.toDayMonthFormat
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Handles periodic clock updates based on the current system time and a list of saved places.
 *
 * This class is designed to be injected and reused in ViewModels that require real-time time zone updates.
 *
 * ## Responsibilities:
 * - Launches a background coroutine job that ticks every minute.
 * - Computes local time for each saved place using its UTC offset.
 * - Emits updated place list and formatted current time/date via a callback.
 *
 * @property clockProvider Provides the current system time in milliseconds.
 * @property coroutineDispatcher Dispatcher used to run the clock update job (e.g., [Dispatchers.Default] or IO).
 */
class ClockUpdaterJobImpl @Inject constructor(
    private val clockProvider: SystemClockHelper,
    @param:IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ClockUpdaterJob {

    /**
     * Holds the reference to the active coroutine clock update job.
     * Ensures only one job runs at a time.
     */
    private var updateJob: Job? = null

    /**
     * Starts the periodic clock update job.
     *
     * This method:
     * - Cancels any existing job to prevent duplication.
     * - Launches a coroutine that runs every minute.
     * - Calculates the current system time and formats it for display.
     * - Updates each saved place's local time using its offset.
     * - Invokes [onUpdate] with the updated state.
     *
     * @param scope The [CoroutineScope] used to launch the job (typically the ViewModel's scope).
     * @param savedPlaces List of [PlaceModel] representing saved time zones.
     * @param onUpdate Callback invoked with:
     *  - the updated list of [PlaceModel]s,
     *  - the formatted current time (e.g., "08:45 PM"),
     *  - the formatted date (e.g., "Wed, 20 Aug").
     * @param onError Callback invoked if an error occurs during local time calculation.
     */
    override fun startClockUpdaterJob(
        scope: CoroutineScope,
        savedPlaces: List<PlaceModel>,
        onUpdate: (List<PlaceModel>, String, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        stopClockUpdaterJob() // Cancel any previous job

        updateJob = scope.launch(coroutineDispatcher) {
            while (isActive) {
                val now = clockProvider.getCurrentTime()
                val formattedTime = now.toClockTimeFormat()
                val formattedDate = now.toDayMonthFormat()

                val updatedPlaces = savedPlaces.map {
                    it.copy(currentTime = calculateLocalTime(now, it.offsetSeconds, onError))
                }

                onUpdate(updatedPlaces, formattedTime, formattedDate)

                val delayMillis = TimeUnit.MINUTES.toMillis(1) - (now % TimeUnit.MINUTES.toMillis(1))
                delay(delayMillis)
            }
        }
    }

    /**
     * Cancels and clears the currently running update job, if any.
     *
     * This is important to prevent resource leaks and multiple update loops.
     */
    override fun stopClockUpdaterJob() {
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Calculates the local time string based on the given base time and UTC offset.
     *
     * If calculation fails (e.g., invalid offset), the [onError] callback is triggered,
     * and a placeholder string ("--:--") is returned.
     *
     * @param baseMillis Current time in milliseconds.
     * @param offsetSeconds Offset from UTC in seconds.
     * @param onError Callback invoked when an exception occurs.
     * @return Formatted time string (e.g., "08:30 PM") or "--:--" on failure.
     */
    private fun calculateLocalTime(
        baseMillis: Long,
        offsetSeconds: Long,
        onError: (Throwable) -> Unit
    ): String {
        return runCatching {
            val zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds.toInt())
            val localTime = Instant.ofEpochMilli(baseMillis).atOffset(zoneOffset)
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            localTime.format(formatter)
        }.getOrElse { e ->
            onError(e)
            "--:--"
        }
    }
}

