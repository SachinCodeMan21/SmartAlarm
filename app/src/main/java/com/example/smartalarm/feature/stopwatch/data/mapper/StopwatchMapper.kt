package com.example.smartalarm.feature.stopwatch.data.mapper

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel


/**
 * Data transformation layer responsible for mapping between Domain models
 * ([StopwatchModel], [StopwatchLapModel]) and Persistence entities
 * ([StopwatchStateEntity], [StopwatchLapEntity]).
 *
 * This object acts as a 'Corruption Barrier,' isolating the Domain layer from
 * database-specific constraints like Primary Keys and relational structures.
 * This ensures that schema changes do not impact core business logic.
 */
object StopwatchMapper {

    /**
     * Converts a database Relation DTO into a unified Domain model.
     * Encapsulates relational logic, providing a complete model to the Repository
     * without exposing underlying SQL JOIN or Relation structures.
     */
    fun StopwatchWithLaps.toDomainModel(): StopwatchModel {
        return stopwatch.toDomainModel(laps)
    }

    /**
     * Maps a Domain model to a persistence-ready Entity.
     * Re-attaches the persistence ID to ensure the Data layer can perform
     * 'Upsert' operations correctly without creating duplicate records.
     * * @param id The primary key used for database persistence (defaults to singleton ID).
     */
    fun StopwatchModel.toEntity(id: Int = 1): StopwatchStateEntity = StopwatchStateEntity(
        id = id,
        startTimeMillis = startTime,
        elapsedTimeMillis = elapsedTime,
        lastStoppedAt = endTime,
        isRunning = isRunning,
        totalLaps = lapCount
    )

    /**
     * Reconstructs a Domain model from flat persistence entities.
     * Discards database identifiers to ensure the Domain layer remains
     * focused on business-relevant properties (timestamps, counts).
     */
    fun StopwatchStateEntity.toDomainModel(laps: List<StopwatchLapEntity>): StopwatchModel =
        StopwatchModel(
            startTime = startTimeMillis,
            elapsedTime = elapsedTimeMillis,
            endTime = lastStoppedAt,
            isRunning = isRunning,
            lapCount = totalLaps,
            lapTimes = laps.map { it.toDomainModel() }
        )

    /**
     * Prepares a Domain Lap for persistent storage.
     * * @param stopwatchId Foreign key linking the lap to the parent session.
     * @note Hardcoded '0' ID allows Room's @PrimaryKey(autoGenerate = true)
     * to handle unique row identifier generation.
     */
    fun StopwatchLapModel.toEntity(stopwatchId: Int): StopwatchLapEntity =
        StopwatchLapEntity(
            id = 0,
            stopwatchId = stopwatchId,
            lapIndex = lapIndex,
            lapStartTimeMillis = lapStartTimeMillis,
            lapElapsedTimeMillis = lapElapsedTimeMillis,
            lapEndTimeMillis = lapEndTimeMillis
        )

    /**
     * Maps a database Lap record to a Domain representation.
     * Relies on [StopwatchLapModel.lapIndex] for identification within the
     * Domain layer, providing stable identity across database migrations.
     */
    fun StopwatchLapEntity.toDomainModel(): StopwatchLapModel =
        StopwatchLapModel(
            lapIndex = lapIndex,
            lapStartTimeMillis = lapStartTimeMillis,
            lapElapsedTimeMillis = lapElapsedTimeMillis,
            lapEndTimeMillis = lapEndTimeMillis
        )
}