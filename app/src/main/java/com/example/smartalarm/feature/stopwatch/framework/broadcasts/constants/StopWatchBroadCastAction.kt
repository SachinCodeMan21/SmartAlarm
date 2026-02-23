package com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants

import com.example.smartalarm.core.utility.Constants.PACKAGE

/**
 * Defines broadcast action constants used by the stopwatch's Broadcast Receiver & foreground service.
 *
 * These actions are used to control the stopwatch via broadcast intents
 * (e.g., from notification actions or other components).
 */
object StopWatchBroadCastAction {

    /** Action to start the stopwatch foreground service. */
    const val START_FOREGROUND = "$PACKAGE.STOPWATCH_START_FOREGROUND"

    /** Action to pause the stopwatch. */
    const val PAUSE = "$PACKAGE.STOPWATCH_PAUSE"

    /** Action to record a lap while the stopwatch is running. */
    const val LAP = "$PACKAGE.STOPWATCH_LAP"

    /** Action to restart the stopwatch after being paused or reset. */
    const val RESUME = "$PACKAGE.STOPWATCH_RESUME"

    /** Action to reset the stopwatch to its initial state. */
    const val RESET = "$PACKAGE.STOPWATCH_RESET"

    /** Action to stop the foreground service hosting the stopwatch. */
    const val STOP_FOREGROUND = "$PACKAGE.STOPWATCH_STOP_FOREGROUND"
}
