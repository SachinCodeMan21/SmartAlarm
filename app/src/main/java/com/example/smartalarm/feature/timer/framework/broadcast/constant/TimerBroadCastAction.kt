package com.example.smartalarm.feature.timer.framework.broadcast.constant

import com.example.smartalarm.core.utility.Constants.PACKAGE

object TimerBroadCastAction {
    const val ACTION_START = "$PACKAGE.action.TIMER_START"
    const val ACTION_PAUSE = "$PACKAGE.action.TIMER_PAUSE"

    const val ACTION_SNOOZE = "$PACKAGE.action.TIMER_SNOOZE"
    const val ACTION_RESUME = "$PACKAGE.action.TIMER_RESUME"
    const val ACTION_STOP = "$PACKAGE.action.TIMER_STOP"
    const val ACTION_STOP_ALL_ACTIVE_TIMERS = "$PACKAGE.action.TIMER_STOP_ALL_ACTIVE_TIMERS"
    const val ACTION_STOP_ALL_COMPLETED_TIMERS = "$PACKAGE.action.TIMER_STOP_ALL_COMPLETED_TIMERS"
    const val ACTION_STOP_FOREGROUND_TIMER = "$PACKAGE.action.TIMER_STOP_TIMER_FOREGROUND"
    const val ACTION_TIMER_TIMEOUT = "$PACKAGE.action.TIMER_TIMEOUT"
    const val ACTION_TIMER_COMPLETED = "$PACKAGE.action.TIMER_COMPLETED"
    const val ACTION_AUTO_CLEANUP = "$PACKAGE.action.TIMER_AUTO_CLEANUP"
}

