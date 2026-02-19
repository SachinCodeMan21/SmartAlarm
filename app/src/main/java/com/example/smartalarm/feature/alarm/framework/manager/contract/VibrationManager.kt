package com.example.smartalarm.feature.alarm.framework.manager.contract

import android.os.VibrationEffect


/**
 * Interface defining the methods for controlling the vibration of the device.
 *
 * This interface provides methods to start, stop, and perform custom vibration patterns on the device.
 */
interface VibrationManager {

    /**
     * Starts a continuous vibration with a repeating pattern.
     */
    fun startVibration()

    /**
     * Stops any ongoing vibration.
     */
    fun stopVibration()

    /**
     * Vibrates the device once for the given duration and amplitude.
     *
     * @param durationMs The duration of the vibration in milliseconds.
     * @param amplitude The amplitude (intensity) of the vibration. Defaults to [VibrationEffect.DEFAULT_AMPLITUDE].
     */
    fun vibrateOneShot(durationMs: Long = 100L, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE)
}