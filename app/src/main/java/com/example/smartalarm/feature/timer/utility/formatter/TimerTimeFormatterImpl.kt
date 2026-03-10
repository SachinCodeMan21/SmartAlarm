package com.example.smartalarm.feature.timer.utility.formatter

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import javax.inject.Inject

class TimerTimeFormatterImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter
) : TimerTimeFormatter {

    override fun formatStringDigitsToTimerTextFormat(input: String): String {

        if (input.isEmpty()) {
            return resourceProvider.getString(R.string.default_timer_time)
        }

        val padded = input.padStart(6, '0')

        val hours = numberFormatter.formatLocalizedNumber(
            padded.substring(0, 2).toLong(),
            true
        )

        val minutes = numberFormatter.formatLocalizedNumber(
            padded.substring(2, 4).toLong(),
            true
        )

        val seconds = numberFormatter.formatLocalizedNumber(
            padded.substring(4, 6).toLong(),
            true
        )

        return resourceProvider.getString(
            R.string.formatted_timer_time,
            hours,
            minutes,
            seconds
        )
    }

    override fun formatStringDigitsToMillis(input: String): Long {

        val padded = input.padStart(6, '0')

        val hours = padded.substring(0, 2).toInt()
        val minutes = padded.substring(2, 4).toInt()
        val seconds = padded.substring(4, 6).toInt()

        return (hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    override fun formatMillisToTimerTextFormat(timerTimeMillis: Long): String {

        val totalSec = timerTimeMillis / 1000

        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60

        val h = numberFormatter.formatLocalizedNumber(hours, true)
        val m = numberFormatter.formatLocalizedNumber(minutes, true)
        val s = numberFormatter.formatLocalizedNumber(seconds, true)

        return resourceProvider.getString(
            R.string.formatted_timer_time,
            h,
            m,
            s
        )
    }
}