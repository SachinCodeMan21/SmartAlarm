package com.example.smartalarm.feature.clock.utility

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import java.util.Calendar
import javax.inject.Inject

class ClockTimeFormatterImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter
) : ClockTimeFormatter {

    override fun formatClockTime(timeInMillis: Long): String {

        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val amPm = if (hour < 12)
            resourceProvider.getString(R.string.am)
        else
            resourceProvider.getString(R.string.pm)

        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        val localizedHour =
            numberFormatter.formatLocalizedNumber(hour12.toLong(), true)

        val localizedMinute =
            numberFormatter.formatLocalizedNumber(minute.toLong(), true)

        return "$localizedHour:$localizedMinute $amPm"
    }

    override fun getPlaceFormattedLocalTime(shiftedMillis: Long): String {

        val totalSeconds = shiftedMillis / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60

        val hour24 = (totalHours % 24).toInt()
        val minute = (totalMinutes % 60).toInt()

        val amPm = if (hour24 < 12)
            resourceProvider.getString(R.string.am)
        else
            resourceProvider.getString(R.string.pm)

        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }

        val localizedHour =
            numberFormatter.formatLocalizedNumber(hour12.toLong(), true)

        val localizedMinute =
            numberFormatter.formatLocalizedNumber(minute.toLong(), true)

        return "$localizedHour:$localizedMinute $amPm"
    }

    override fun formatDayMonth(dateInMillis: Long): String {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }

        val day = numberFormatter.formatLocalizedNumber(
            calendar.get(Calendar.DAY_OF_MONTH).toLong(),
            false
        )

        val month = resourceProvider
            .getStringArray(R.array.month_names)[calendar.get(Calendar.MONTH)]

        val year = numberFormatter.formatLocalizedNumber(
            calendar.get(Calendar.YEAR).toLong(),
            false
        )

        val weekday = resourceProvider
            .getStringArray(R.array.full_weekdays)[calendar.get(Calendar.DAY_OF_WEEK) - 1]

        return "$weekday, $day $month $year"
    }
}