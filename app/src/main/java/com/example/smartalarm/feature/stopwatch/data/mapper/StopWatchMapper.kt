package com.example.smartalarm.feature.stopwatch.data.mapper

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel

/**
 * Mapper object responsible for converting between domain models
 * ([StopwatchModel], [StopWatchLapModel]) and database entities
 * ([StopWatchEntity], [StopWatchLapEntity]).
 *
 * This separation ensures clean boundaries between the data layer and
 * domain/business logic layer by providing precise and maintainable transformations.
 */
object StopWatchMapper {

    /**
     * Converts a [StopwatchModel] domain model into a [StopWatchEntity] for persistence.
     *
     * @receiver The domain model to convert.
     * @return A corresponding database entity suitable for storage.
     */
    fun StopwatchModel.toEntity(): StopWatchEntity = StopWatchEntity(
        id = id,
        startTime = startTime,
        elapsedTime = elapsedTime,
        endTime = endTime,
        isRunning = isRunning,
        lapCount = lapCount,
    )

    /**
     * Converts a [StopWatchEntity] and its associated lap entities into a [StopwatchModel].
     *
     * @receiver The database entity to convert.
     * @param laps List of lap entities related to the stopwatch.
     * @return A fully populated domain model including lap data.
     */
    fun StopWatchEntity.toDomainModel(laps: List<StopWatchLapEntity>): StopwatchModel = StopwatchModel(
        id = id,
        startTime = startTime,
        elapsedTime = elapsedTime,
        endTime = endTime,
        isRunning = isRunning,
        lapCount = lapCount,
        lapTimes = laps.map { it.toDomainModel() }
    )

    /**
     * Converts a [StopWatchLapModel] domain model into a [StopWatchLapEntity] for persistence.
     *
     * @receiver The lap domain model to convert.
     * @param stopwatchId The ID of the parent stopwatch entity.
     * @return The corresponding lap entity suitable for storage.
     */
    fun StopWatchLapModel.toEntity(stopwatchId: Int): StopWatchLapEntity = StopWatchLapEntity(
        stopwatchId = stopwatchId,
        lapIndexId = lapIndex,
        lapStartTime = lapStartTime,
        lapElapsedTime = lapElapsedTime,
        lapEndTime = lapEndTime
    )

    /**
     * Converts a [StopWatchLapEntity] database entity into a [StopWatchLapModel].
     *
     * @receiver The lap entity to convert.
     * @return The corresponding lap domain model.
     */
    fun StopWatchLapEntity.toDomainModel(): StopWatchLapModel = StopWatchLapModel(
        lapIndex = lapIndexId,
        lapStartTime = lapStartTime,
        lapElapsedTime = lapElapsedTime,
        lapEndTime = lapEndTime
    )

}
