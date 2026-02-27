package com.example.smartalarm.feature.clock.presentation.mapper

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.presentation.model.PlaceUiModel
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.abs

object PlaceUiMapper {

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
            currentTime = domain.currentTime,
            timeDifference = getTimeDifferenceString(domain.offsetSeconds.toLong())
        )
    }

    /**
     * Converts a list of [PlaceModel] to a list of [PlaceUiModel].
     */
    fun mapToUiList(domainList: List<PlaceModel>): List<PlaceUiModel> {
        return domainList.map { mapToUi(it) }
    }

    private fun getTimeDifferenceString(targetOffsetSeconds: Long): String {
        val systemZone = ZoneId.systemDefault()
        val currentOffsetSeconds = systemZone.rules.getOffset(Instant.now()).totalSeconds.toLong()

        val diffSeconds = targetOffsetSeconds - currentOffsetSeconds
        val diffHours = diffSeconds / 3600.0
        val absoluteHours = abs(diffHours)

        val timeLabel = when {
            diffHours == 0.0 -> "Same time"
            diffHours > 0 -> "${formatHours(absoluteHours)} ahead"
            else -> "${formatHours(absoluteHours)} behind"
        }

        val targetTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(targetOffsetSeconds)
        val localTime = ZonedDateTime.now()

        val dayLabel = when {
            targetTime.dayOfYear > localTime.dayOfYear -> "Tomorrow"
            targetTime.dayOfYear < localTime.dayOfYear -> "Yesterday"
            else -> "Today"
        }

        return "$dayLabel, $timeLabel"
    }

    private fun formatHours(hours: Double): String =
        if (hours % 1 == 0.0) "${hours.toInt()}h" else "${hours}h"
}