package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult

/**
 * Interface defining the use case for saving or updating an alarm and triggering related actions.
 *
 * This use case involves saving or updating an alarm and scheduling it for the specified time.
 * If the alarm is enabled, it will also schedule the alarm and send notifications to the user.
 * The result returned is a [MyResult] containing a string that represents how long until the alarm
 * triggers (or an empty string if the alarm is disabled).
 */
interface PostSaveOrUpdateAlarmUseCase {

    /**
     * Saves or updates the alarm and performs actions such as scheduling and notifying the user.
     *
     * @param alarm The [AlarmModel] to be saved or updated.
     * @return A [MyResult] containing a string representing how long until the alarm triggers,
     *         or an empty string if the alarm is disabled.
     */
    operator fun invoke(alarm: AlarmModel): MyResult<String, DataError>
}

