package com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants

import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.receivers.StopwatchReceiver
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService

/**
 * Constants defining the operational contract for stopwatch-related intent signaling.
 * * These actions are the primary communication tokens used by the [StopwatchReceiver]
 * and [StopwatchService] to synchronize state changes triggered by external UI
 * surfaces like system notifications.
 */
object StopWatchBroadCastAction {

    /** Signals the service to promote to a foreground state and start the timer engine. */
    const val START_FOREGROUND = "$PACKAGE.STOPWATCH_START_FOREGROUND"

    /** Suspends the active timer engine and persists the current elapsed interval. */
    const val PAUSE = "$PACKAGE.STOPWATCH_PAUSE"

    /** Triggers a split-time calculation and records the current lap interval. */
    const val LAP = "$PACKAGE.STOPWATCH_LAP"

    /** Reactivates the timer engine from a suspended state. */
    const val RESUME = "$PACKAGE.STOPWATCH_RESUME"

    /** Restores the stopwatch session to its zeroed state and clears associated records. */
    const val RESET = "$PACKAGE.STOPWATCH_RESET"

    /** Demotes the service to a background state and dismisses active notifications. */
    const val STOP_FOREGROUND = "$PACKAGE.STOPWATCH_STOP_FOREGROUND"
}
