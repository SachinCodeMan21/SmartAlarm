package com.example.smartalarm.feature.timer.data.mapper

import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerUiModel


/**
 * Mapper object for converting between different representations of a Timer.
 * It provides functions to convert between domain models, UI models, and database entities.
 *
 * **Functions:**
 * - `toEntity()`: Converts a `TimerModel` to a `TimerEntity`, which is used for persistence in the database.
 * - `toDomainModel()`: Converts a `TimerEntity` (from the database) back to a `TimerModel`, which represents the
 *   data in the domain layer.
 * - `toUiModel()`: Converts a `TimerModel` to a `ShowTimerUiModel`, which is used for displaying the timer in the UI.
 * - `toDomainModel()` (overload): Converts a `ShowTimerUiModel` (from the UI layer) back into a `TimerModel` for
 *   manipulation in the domain layer.
 *
 * **Purpose:**
 * This object simplifies the process of converting timer data between different layers of the application
 * (UI, domain, and persistence). This separation allows for clear decoupling of concerns, making the code
 * easier to maintain and test.
 *
 * **Usage:**
 * - `TimerModel` represents the data in the domain layer.
 * - `TimerEntity` is used for storing the timer data in a local database.
 * - `ShowTimerUiModel` is used for displaying the timer data in the UI layer.
 */
object TimerMapper {

    /**
     * Converts a [TimerModel] to a [TimerEntity] for persistence in the database.
     */
    fun TimerModel.toEntity(): TimerEntity {
        return TimerEntity(
            id = this.timerId,
            startTimeMillis = this.startTime,
            remainingMillis = this.remainingTime,
            endTimeMillis = this.endTime,
            targetDurationMillis = this.targetTime,
            isTimerRunning = this.isTimerRunning,
            isTimerSnoozed = this.isTimerSnoozed,
            snoozedTargetDurationMillis = this.snoozedTargetTime,
            state = this.status
        )
    }

    /**
     * Converts a [TimerEntity] from the database to a [TimerModel] for the domain layer.
     */
    fun TimerEntity.toDomainModel(): TimerModel {
        return TimerModel(
            timerId = this.id,
            startTime = this.startTimeMillis,
            remainingTime = this.remainingMillis,
            endTime = this.endTimeMillis,
            targetTime = this.targetDurationMillis,
            isTimerRunning = this.isTimerRunning,
            isTimerSnoozed = this.isTimerSnoozed,
            snoozedTargetTime = this.snoozedTargetDurationMillis,
            status = this.state
        )
    }

    /**
     * Converts a [TimerModel] to a [ShowTimerUiModel] for displaying in the UI.
     */
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
            state = timerModel.status
        )
    }

    /**
     * Converts a [ShowTimerUiModel] from the UI layer to a [TimerModel] for the domain layer.
     */
    fun toDomainModel(timerUiModel: ShowTimerUiModel): TimerModel {
        return TimerModel(
            timerId = timerUiModel.timerId,
            startTime = timerUiModel.startTime,
            remainingTime = timerUiModel.remainingTime,
            endTime = timerUiModel.endTime,
            targetTime = timerUiModel.targetTime,
            isTimerRunning = timerUiModel.isRunning,
            isTimerSnoozed = timerUiModel.isSnoozed,
            snoozedTargetTime = timerUiModel.snoozedTargetTime,
            status = timerUiModel.state
        )
    }
}

