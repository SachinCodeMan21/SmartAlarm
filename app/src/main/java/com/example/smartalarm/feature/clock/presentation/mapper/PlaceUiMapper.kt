package com.example.smartalarm.feature.clock.presentation.mapper

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.presentation.model.PlaceUiModel
import com.example.smartalarm.feature.clock.utility.ClockTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.abs

class PlaceUiMapper @Inject constructor(
    private val timeFormatter: ClockTimeFormatter,
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter,
    private val systemClockHelper: SystemClockHelper
) {

    /**
     * Maps a domain [PlaceModel] to a [PlaceUiModel] for UI display.
     *
     * @param domain PlaceModel from domain layer.
     * @return PlaceUiModel formatted for UI.
     */
    fun mapToUi(domain: PlaceModel): PlaceUiModel {
        return PlaceUiModel(
            id = domain.id,
            name = domain.primaryName,
            fullName = domain.fullName,
            currentTime = getLocalizedCurrentTime(domain.offsetSeconds),
            timeDifference = getTimeDifferenceString(domain.offsetSeconds.toLong())
        )
    }

    /**
     * Converts a list of [PlaceModel] to a list of [PlaceUiModel].
     */
    fun mapToUiList(domainList: List<PlaceModel>): List<PlaceUiModel> {
        return domainList.map { mapToUi(it) }
    }

    private fun getLocalizedCurrentTime(offsetTime: Int): String {
        val placeCurrentLocalTime = systemClockHelper.getPlaceCurrentLocalTime(offsetTime)
        val formattedLocalTime = timeFormatter.getPlaceFormattedLocalTime(placeCurrentLocalTime)
        return formattedLocalTime
    }

    private fun getTimeDifferenceString(targetOffsetSeconds: Long): String {
        val systemZone = ZoneId.systemDefault()
        val currentOffsetSeconds = systemZone.rules.getOffset(Instant.now()).totalSeconds.toLong()

        val diffSeconds = targetOffsetSeconds - currentOffsetSeconds
        val diffHours = diffSeconds / 3600.0
        val absoluteHours = abs(diffHours)

        // Get localized time label
        val timeLabel = when {
            diffHours == 0.0 -> resourceProvider.getString(R.string.time_same)
            diffHours > 0 -> resourceProvider.getString(R.string.time_ahead, formatHours(absoluteHours))
            else -> resourceProvider.getString(R.string.time_behind, formatHours(absoluteHours))
        }

        val targetTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(targetOffsetSeconds)
        val localTime = ZonedDateTime.now()

        // Get localized day label
        val dayLabel = when {
            targetTime.dayOfYear > localTime.dayOfYear -> resourceProvider.getString(R.string.time_today)
            targetTime.dayOfYear < localTime.dayOfYear -> resourceProvider.getString(R.string.time_tomorrow)
            else -> resourceProvider.getString(R.string.time_today)
        }

        return "$dayLabel, $timeLabel"
    }

    private fun formatHours(hours: Double): String {
        // Use NumberFormatter to localize the hours
        val formattedHours = numberFormatter.formatLocalizedNumber(hours.toInt().toLong(), false)

        // Check if hours is a whole number or has a decimal part
        return if (hours % 1 == 0.0) {
            "$formattedHours ${resourceProvider.getString(R.string.hour)}"
        } else {
            // For fractional hours, display the full number with decimals, localizing as well
            val formattedDecimalHours = numberFormatter.formatLocalizedNumber(hours.toLong(), false)
            "$formattedDecimalHours ${resourceProvider.getString(R.string.hour)}"
        }
    }

}