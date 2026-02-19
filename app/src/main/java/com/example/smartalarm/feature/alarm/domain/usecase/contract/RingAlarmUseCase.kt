package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel



/**
 * Defines the use case for ringing an alarm.
 * This operation updates the alarm state and triggers the necessary actions,
 * such as playing the ringtone, enabling vibration, and scheduling a timeout.
 */
interface RingAlarmUseCase {

    /**
     * Rings the alarm, updates its state, and performs associated actions (e.g., sound, vibration).
     *
     * @param alarm The [AlarmModel] object containing the alarm details.
     * @return [Result] A [Result.Success] containing the updated [AlarmModel] if successful, or [Result.Error] if an error occurs.
     */
    suspend operator fun invoke(alarm: AlarmModel): Result<AlarmModel>
}

