package com.example.smartalarm.feature.alarm.domain.enums

/**
 * Represents the various states an alarm can be in during its lifecycle.
 *
 * This enum is used to track and manage the current status of an alarm,
 * which can influence UI behavior, notifications, and scheduling logic.
 *
 * States:
 * - [UPCOMING]: The alarm is scheduled and waiting to go off.
 * - [RINGING]: The alarm is currently ringing.
 * - [SNOOZED]: The alarm has been snoozed and will ring again after the snooze interval.
 * - [MISSED]: The alarm rang but was neither dismissed nor snoozed by the user.
 * - [STOPPED]: The alarm was stopped by the user or system (e.g., task completed or dismissed).
 * - [EXPIRED]: The alarm time has passed and it's no longer relevant (e.g., auto-dismissed after timeout).
 */
enum class AlarmState {
    UPCOMING,
    RINGING,
    SNOOZED,
    PAUSED,
    RESUMED,
    MISSED,
    STOPPED,
    EXPIRED
}