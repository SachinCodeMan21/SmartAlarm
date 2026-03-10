package com.example.smartalarm.feature.stopwatch.utility

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import javax.inject.Inject

class StopwatchTimeFormatterImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter
) : StopwatchTimeFormatter {

    override fun formatMainDisplay(durationMillis: Long, includeMillis: Boolean): String {
        val hours = durationMillis / 3_600_000
        val minutes = (durationMillis % 3_600_000) / 60_000
        val seconds = (durationMillis % 60_000) / 1_000

        val localizedHour = numberFormatter.formatLocalizedNumber(hours, true)
        val localizedMinute = numberFormatter.formatLocalizedNumber(minutes, true)
        val localizedSecond = numberFormatter.formatLocalizedNumber(seconds, true)

        return if (includeMillis) {
            val localizedMillis = formatFractionalSeconds(durationMillis)
            // Uses a localized string resource for the full format (e.g., "%1$s:%2$s:%3$s:%4$s")
            resourceProvider.getString(
                R.string.full_formatted_stopwatch_time,
                localizedHour, localizedMinute, localizedSecond, localizedMillis
            )
        } else {
            when {
                hours > 0 -> resourceProvider.getString(
                    R.string.hour_formatted_stopwatch_time,
                    localizedHour, localizedMinute, localizedSecond
                )
                minutes > 0 -> resourceProvider.getString(
                    R.string.min_formatted_stopwatch_time,
                    localizedMinute, localizedSecond
                )
                else -> resourceProvider.getString(
                    R.string.sec_formatted_stopwatch_time,
                    localizedSecond
                )
            }
        }
    }
    override fun formatFractionalSeconds(durationMillis: Long): String {
        // Extracts deciseconds (0-99) for a clean visual flow
        val deciseconds = (durationMillis % 1000) / 10
        return numberFormatter.formatLocalizedNumber(deciseconds, true)
    }

}