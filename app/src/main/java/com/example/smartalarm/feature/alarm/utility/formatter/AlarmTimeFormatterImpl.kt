package com.example.smartalarm.feature.alarm.utility.formatter

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import java.util.Calendar
import javax.inject.Inject

class AlarmTimeFormatterImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val numberFormatter: NumberFormatter
) : AlarmTimeFormatter {

    override fun formatToAlarmTime(hour: Int, minute: Int): String {

        val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        val amPmString = if (hour < 12) {
            resourceProvider.getString(R.string.am)
        } else {
            resourceProvider.getString(R.string.pm)
        }

        val localizedHour =
            numberFormatter.formatLocalizedNumber(formattedHour.toLong(), true)

        val localizedMinute =
            numberFormatter.formatLocalizedNumber(minute.toLong(), true)

        return "$localizedHour:$localizedMinute $amPmString"
    }

    override fun getFormattedDayAndTime(hour: Int, minute: Int): String {

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        val day = resourceProvider
            .getStringArray(R.array.full_weekdays)[currentDay - 1]
            .take(3)

        val formattedTime = formatToAlarmTime(hour, minute)

        return "$day, $formattedTime"
    }

    override fun getFormattedDayAndTime(alarmMillis: Long): String {

        val now = Calendar.getInstance()

        val alarmCal = Calendar.getInstance().apply {
            timeInMillis = alarmMillis
        }

        val label = when {
            isSameDay(now, alarmCal) ->
                resourceProvider.getString(R.string.today)

            isTomorrow(now, alarmCal) ->
                resourceProvider.getString(R.string.tomorrow)

            else -> {
                val dayIndex = alarmCal.get(Calendar.DAY_OF_WEEK) - 1
                resourceProvider
                    .getStringArray(R.array.full_weekdays)[dayIndex]
                    .take(3)
            }
        }

        val hour = alarmCal.get(Calendar.HOUR_OF_DAY)
        val minute = alarmCal.get(Calendar.MINUTE)

        return "$label, ${formatToAlarmTime(hour, minute)}"
    }

    private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isTomorrow(today: Calendar, target: Calendar): Boolean {
        val tomorrow = today.clone() as Calendar
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        return isSameDay(tomorrow, target)
    }
}