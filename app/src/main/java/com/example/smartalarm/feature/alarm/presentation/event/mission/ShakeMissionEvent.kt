package com.example.smartalarm.feature.alarm.presentation.event.mission

import com.example.smartalarm.feature.alarm.domain.model.Mission

/**
 * Represents the different types of events that can occur during a shake mission.
 *
 * This sealed class is used to handle shake mission-related actions such as initializing
 * a shake mission or responding to changes in the device's acceleration.
 */
sealed class ShakeMissionEvent {

    /**
     * Event triggered when a shake mission is initialized.
     *
     * @property mission The [Mission] instance that defines the mission's parameters and objectives.
     */
    data class InitializeMission(val mission: Mission) : ShakeMissionEvent()

    /**
     * Event triggered when the device's acceleration value changes.
     *
     * @property acceleration The current acceleration value measured by the device's sensors.
     */
    data class AccelerationChanged(val acceleration: Double) : ShakeMissionEvent()
}
