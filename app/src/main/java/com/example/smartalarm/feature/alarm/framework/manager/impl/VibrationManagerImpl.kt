package com.example.smartalarm.feature.alarm.framework.manager.impl

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implementation of [VibrationManager] for controlling the device's vibration.
 *
 * This class provides functionality to start continuous vibration, stop vibration, and perform one-shot vibrations
 * with custom duration and amplitude.
 */
class VibrationManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : VibrationManager {

    // Lazy initialization of the vibrator object based on Android version
    @Suppress("DEPRECATION")
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android S (API 31) and above, use VibratorManager
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            // For below Android S, use the traditional Vibrator service
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Starts a continuous vibration with a repeating pattern.
     *
     * The vibration pattern is as follows:
     * - Vibrates for 1 second.
     * - Pauses for 1 second.
     * - Repeats indefinitely.
     */
    override fun startVibration() {
        if (!vibrator.hasVibrator()) return

        // Define a repeating vibration pattern: vibrate 1s, pause 1s, repeat indefinitely
        val pattern = longArrayOf(0, 1000, 1000)
        val effect = VibrationEffect.createWaveform(pattern, 0)
        vibrator.vibrate(effect)  // Start vibration with the specified pattern
    }

    /**
     * Stops any ongoing vibration immediately.
     */
    override fun stopVibration() {
        vibrator.cancel()  // Cancel the ongoing vibration
    }

    /**
     * Vibrates the device once for the specified duration and amplitude.
     *
     * @param durationMs The duration of the vibration in milliseconds (default: 100ms).
     * @param amplitude The amplitude (intensity) of the vibration (default: [VibrationEffect.DEFAULT_AMPLITUDE]).
     */
    override fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        if (!vibrator.hasVibrator()) return

        // Create a one-shot vibration effect with the specified duration and amplitude
        val effect = VibrationEffect.createOneShot(durationMs, amplitude)
        vibrator.vibrate(effect)  // Trigger one-shot vibration
    }
}