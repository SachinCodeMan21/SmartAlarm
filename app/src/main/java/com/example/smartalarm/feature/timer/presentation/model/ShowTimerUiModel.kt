package com.example.smartalarm.feature.timer.presentation.model

import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.utility.toFormattedTimerTime


data class ShowTimerUiModel(
    val timerId : Int = 0,
    val startTime : Long = 0,
    val remainingTime : Long = 0,
    val endTime : Long = 0,
    val targetTime : Long = 0,
    val isRunning : Boolean = false,
    val isSnoozed : Boolean = false,
    val snoozedTargetTime: Long = 0,
    val state : TimerState
){
    fun getProgress() : Int {
        val total = if (isSnoozed) snoozedTargetTime else targetTime
        return ((remainingTime/total.toFloat()) * 100).toInt()
    }
}
