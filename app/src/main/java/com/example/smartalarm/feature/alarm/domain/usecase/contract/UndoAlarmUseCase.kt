package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.model.Result


/**
 * Interface defining the use case for undoing an alarm action, such as restoring  a previously deleted alarm.
 *
 * This use case encapsulates the logic for restoring the alarm's previous state, scheduling the alarm,
 * and ensuring that the alarm is re-enabled with the appropriate time and notification.
 */
interface UndoAlarmUseCase {

    /**
     * Restores the alarm by saving it back into the system and scheduling it if enabled.
     *
     * @param undoAlarm The alarm model that needs to be restored.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(undoAlarm: AlarmModel): Result<String>
}
