package com.example.smartalarm.feature.timer.utility.formatter

interface TimerTimeFormatter {

    fun formatStringDigitsToTimerTextFormat(input: String): String

    fun formatStringDigitsToMillis(input: String): Long

    fun formatMillisToTimerTextFormat(timerTimeMillis: Long): String
}