package com.example.smartalarm.feature.clock.utility

interface ClockTimeFormatter {

    fun formatClockTime(timeInMillis: Long): String

    fun getPlaceFormattedLocalTime(shiftedMillis: Long): String

    fun formatDayMonth(dateInMillis: Long): String
}