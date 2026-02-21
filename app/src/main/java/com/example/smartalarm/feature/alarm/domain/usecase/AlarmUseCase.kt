package com.example.smartalarm.feature.alarm.domain.usecase

import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAllAlarmsUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SwipedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.ToggleAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UndoAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import javax.inject.Inject


/**
* @param getAllAlarmsUseCase Use case for retrieving all alarms from the data source.
* @param saveAlarmUseCase Use case for saving a new alarm.
* @param updateAlarmUseCase Use case for updating an existing alarm.
* @param deleteAlarmUseCase Use case for deleting an alarm.
* @param toggleAlarmUseCase Use case for toggling the enabled state of an alarm.
* @param undoAlarmUseCase Use case for undoing the deletion of an alarm.
* @param swipedAlarmUseCase Use case for handling swiped alarms (i.e., delete actions).*/
data class AlarmUseCase @Inject constructor(
    val getAllAlarmsUseCase: GetAllAlarmsUseCase,
    val saveAlarmUseCase: SaveAlarmUseCase,
    val updateAlarmUseCase: UpdateAlarmUseCase,
    val deleteAlarmUseCase: DeleteAlarmUseCase,
    val toggleAlarmUseCase: ToggleAlarmUseCase,
    val undoAlarmUseCase: UndoAlarmUseCase,
    val swipedAlarmUseCase: SwipedAlarmUseCase,
)
