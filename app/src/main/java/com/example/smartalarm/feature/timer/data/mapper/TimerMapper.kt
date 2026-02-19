package com.example.smartalarm.feature.timer.data.mapper

import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerUiModel


object TimerMapper {

    fun TimerModel.toEntity(): TimerEntity {
        return TimerEntity(
            timerId = this.timerId,
            startTime = this.startTime,
            remainingTime = this.remainingTime,
            endTime = this.endTime,
            targetTime = this.targetTime,
            isTimerRunning = this.isTimerRunning,
            isTimerSnoozed = this.isTimerSnoozed,
            snoozedTargetTime = this.snoozedTargetTime,
            state = this.state.name
        )
    }



    fun TimerEntity.toDomainModel(): TimerModel {
        return TimerModel(
            timerId = this.timerId,
            startTime = this.startTime,
            remainingTime = this.remainingTime,
            endTime = this.endTime,
            targetTime = this.targetTime,
            isTimerRunning = this.isTimerRunning,
            isTimerSnoozed = this.isTimerSnoozed,
            snoozedTargetTime = this.snoozedTargetTime,
            state = TimerState.valueOf(this.state)
        )
    }


    fun toUiModel(timerModel: TimerModel): ShowTimerUiModel {
        return ShowTimerUiModel(
            timerId = timerModel.timerId,
            startTime = timerModel.startTime,
            remainingTime = timerModel.remainingTime,
            endTime = timerModel.endTime,
            targetTime = timerModel.targetTime,
            isRunning = timerModel.isTimerRunning,
            isSnoozed = timerModel.isTimerSnoozed,
            snoozedTargetTime = timerModel.snoozedTargetTime,
            state = timerModel.state
        )
    }

    fun toDomainModel(timerUiModel: ShowTimerUiModel) : TimerModel{

        return TimerModel(
            timerId = timerUiModel.timerId,
            startTime = timerUiModel.startTime,
            remainingTime = timerUiModel.remainingTime,
            endTime = timerUiModel.endTime,
            targetTime = timerUiModel.targetTime,
            isTimerRunning = timerUiModel.isRunning,
            isTimerSnoozed = timerUiModel.isSnoozed,
            snoozedTargetTime = timerUiModel.snoozedTargetTime,
            state = timerUiModel.state
        )
    }

}


