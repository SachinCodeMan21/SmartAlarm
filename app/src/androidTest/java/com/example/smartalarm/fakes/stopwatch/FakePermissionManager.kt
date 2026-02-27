package com.example.smartalarm.fakes.stopwatch

import com.example.smartalarm.core.framework.permission.PermissionManager
import javax.inject.Inject

/**
 * A controllable fake implementation of [PermissionManager] for use in tests.
 *
 * Allows you to explicitly set the return values for each permission check,
 * making your ViewModel or UseCase tests deterministic and fast.
 */
class FakePermissionManager @Inject constructor() : PermissionManager {

    var postNotificationGranted: Boolean = true
    var scheduleExactAlarmGranted: Boolean = true
    var fullScreenIntentGranted: Boolean = true
    var sensorGranted: Boolean = true


    override fun isPostNotificationPermissionGranted(): Boolean {
        return postNotificationGranted
    }

    override fun isScheduleExactAlarmPermissionGranted(): Boolean {
        return scheduleExactAlarmGranted
    }

    override fun isFullScreenNotificationPermissionGranted(): Boolean {
        return fullScreenIntentGranted
    }

    override fun isSensorPermissionGranted(): Boolean {
        return sensorGranted
    }


    /**
     * Convenience method to set all permissions at once.
     */
    fun setAllPermissions(granted: Boolean) {
        postNotificationGranted = granted
        scheduleExactAlarmGranted = granted
        fullScreenIntentGranted = granted
        sensorGranted = granted
    }

    /**
     * Resets all permissions to their default (true) state.
     */
    fun reset() {
        setAllPermissions(true)
    }
}