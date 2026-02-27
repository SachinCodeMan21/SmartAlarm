package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel


/**
 * Interface defining the use case for toggling the alarm's enabled state.
 *
 * This use case encapsulates the logic for enabling or disabling an alarm and managing
 * its associated scheduling and notifications. It ensures that the alarm state is
 * properly updated and that any necessary actions are taken, such as scheduling or canceling the alarm.
 */
interface ToggleAlarmUseCase {

    /**
     * Toggles the enabled state of the alarm and schedules or cancels the alarm accordingly.
     *
     * @param alarmModel The alarm model containing the current alarm details.
     * @param isEnabled A boolean indicating whether the alarm should be enabled or disabled.
     * @return A [MyResult] indicating success or failure of the operation.
     */
    suspend operator fun invoke(alarmModel: AlarmModel, isEnabled: Boolean): MyResult<String, DataError>
}