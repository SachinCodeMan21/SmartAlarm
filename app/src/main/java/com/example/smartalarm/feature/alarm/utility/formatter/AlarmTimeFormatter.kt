package com.example.smartalarm.feature.alarm.utility.formatter

interface AlarmTimeFormatter {

    fun formatToAlarmTime(hour: Int, minute: Int): String

    fun getFormattedDayAndTime(hour: Int, minute: Int): String

    fun getFormattedDayAndTime(alarmMillis: Long): String
}