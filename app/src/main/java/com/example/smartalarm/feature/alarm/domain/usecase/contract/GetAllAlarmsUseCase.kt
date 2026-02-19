package com.example.smartalarm.feature.alarm.domain.usecase.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case interface for retrieving a list of all alarms.
 *
 * This abstraction allows fetching alarms as a reactive stream, enabling the UI or other
 * consumers to react to real-time updates in the alarm data.
 */
interface GetAllAlarmsUseCase {

    /**
     * Returns a [Flow] of a list of [AlarmModel] items.
     *
     * The flow emits updates whenever the alarm list changes in the data source.
     *
     * @return A reactive [Flow] emitting lists of alarms.
     */
    operator fun invoke(): Flow<List<AlarmModel>>
}
