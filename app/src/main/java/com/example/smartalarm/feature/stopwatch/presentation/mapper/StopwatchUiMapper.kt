package com.example.smartalarm.feature.stopwatch.presentation.mapper

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import javax.inject.Inject

/**
 * Mapper object that converts domain models related to the stopwatch
 * into their corresponding UI models for display purposes.
 *
 * This ensures a clear separation between domain logic and UI representation,
 * making the app easier to test and maintain.
 */
class StopwatchUiMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter,
    private val timeFormatter: TimeFormatter
) {

    /**
     * Converts a [StopwatchModel] from the domain layer into a [StopwatchUiModel] for the UI layer.
     *
     * @param model The domain model representing the stopwatch state.
     * @return A [StopwatchUiModel] containing formatted and UI-friendly data.
     */
    fun mapToUiModel(model: StopwatchModel): StopwatchUiModel {
        return StopwatchUiModel(
            secondsText = timeFormatter.formatDurationForStopwatch(durationMillis = model.elapsedTime, includeMillis = false),
            milliSecondsText = timeFormatter.formatMillisForStopwatch(model.elapsedTime),
            isRunning = model.isRunning,
            progress = model.getIndicatorProgress,
            laps = model.lapTimes.map { mapLapToUiModel(it) },
        )
    }


    /**
     * Converts a [StopWatchLapModel] from the domain layer into a [StopwatchLapUiModel] for UI display.
     *
     * @param lap The domain model representing a single lap's timing information.
     * @return A [StopwatchLapUiModel] with formatted strings for use in the UI.
     */
    fun mapLapToUiModel(lap: StopWatchLapModel): StopwatchLapUiModel {
        return StopwatchLapUiModel(
            formattedLapIndex = "${resourceProvider.getString(R.string.lap_index)} ${ numberFormatter.formatLocalizedNumber(lap.lapIndex.toLong(),false)}",
            formattedLapStartTime = timeFormatter.formatDurationForStopwatch(lap.lapStartTime, includeMillis = true),
            formattedLapElapsedTime = timeFormatter.formatDurationForStopwatch(lap.lapElapsedTime,includeMillis = true),
            formattedLapEndTime = timeFormatter.formatDurationForStopwatch(lap.lapEndTime,includeMillis = true)
        )
    }

}