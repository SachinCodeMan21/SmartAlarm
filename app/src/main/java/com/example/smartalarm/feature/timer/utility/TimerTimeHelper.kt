package com.example.smartalarm.feature.timer.utility

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import javax.inject.Inject
import kotlin.math.floor

class TimerTimeHelper @Inject constructor(
    private val clockProvider: SystemClockHelper
) {

    /**
     * Calculates the start time when resuming a timer.
     */
    fun calculateAdjustedStartTime(timer: TimerModel): Long {
        val totalTime = if (timer.isTimerSnoozed) timer.snoozedTargetTime else timer.targetTime
        return clockProvider.getCurrentTime() - (totalTime - timer.remainingTime)
    }

    /**
     * Calculates remaining time using raw system time.
     */
    fun calculatePreciseRemainingTime(timer: TimerModel): Long {
        val totalTime = if (timer.isTimerSnoozed) timer.snoozedTargetTime else timer.targetTime
        return totalTime - (clockProvider.getCurrentTime() - timer.startTime)
    }

    /**
     * Returns elapsed time floored to nearest second (1000ms).
     */
    fun getElapsedTimeRounded(timer: TimerModel): Long {
        val elapsed = clockProvider.getCurrentTime() - timer.startTime
        return floor(elapsed / 1000.0).toLong() * 1000
    }

    /**
     * Returns remaining time with snooze check and rounded elapsed time.
     */
    fun getRemainingTimeConsideringSnooze(timer: TimerModel): Long {
        val total = if (timer.isTimerSnoozed) timer.snoozedTargetTime else timer.targetTime
        return total - getElapsedTimeRounded(timer)
    }

    fun getCurrentTime(): Long {
        return clockProvider.getCurrentTime()
    }
}
