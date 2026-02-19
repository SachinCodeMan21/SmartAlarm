package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel

/**
 * Use case that handles the dismissal of an alarm.
 *
 * This use case is responsible for managing the lifecycle of an alarm when it is dismissed.
 * It ensures that the appropriate actions are taken depending on the alarm's state:
 *
 * - For **one-time alarms**: cancels the alarm, marks it as expired, and stops any associated notifications or schedules.
 * - For **repeating alarms**: cancels the current alarm and reschedules it for the next valid trigger time.
 *
 * The `DismissAlarmUseCase` will handle:
 * 1. Retrieving the alarm from the database using the provided alarmId.
 * 2. Updating the alarm state to either expired or rescheduled, depending on the alarm type.
 * 3. Managing alarm notifications and scheduled timeouts.
 * 4. Ensuring that any necessary system alarms or notifications are properly cleared or rescheduled.
 *
 * This use case is useful in scenarios where an alarm is dismissed manually or automatically based on certain conditions.
 *
 * @param alarmId The unique ID of the alarm to be dismissed.
 * @return The updated [AlarmModel] after the dismissal action, or null if the alarm could not be found or updated.
 */
interface DismissAlarmUseCase {
    suspend operator fun invoke(alarmId: Int): AlarmModel?
}
