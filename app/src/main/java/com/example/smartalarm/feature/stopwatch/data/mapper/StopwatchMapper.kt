package com.example.smartalarm.feature.stopwatch.data.mapper

import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopwatchWithLaps
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel


/**
 * Mapper object responsible for converting between Domain Models
 * ([StopwatchModel], [StopwatchLapModel]) and Data Entities
 * ([StopwatchStateEntity], [StopwatchLapEntity]).
 *
 * ### Why this exists:
 * This mapper acts as a corruption barrier. By separating storage concerns from
 * business logic, we ensure that if the database schema changes, the core
 * stopwatch logic remains untouched.
 *
 * ### Future-Proofing & Scalability:
 * While the application currently operates as a singleton, this mapping strategy
 * allows for a seamless transition to a "Multi-Stopwatch" feature:
 * 1. **Minimal Refactoring**: To support multiple stopwatches, we only need to
 * add an `id` field to the [StopwatchModel] and update this mapper.
 * 2. **UI Isolation**: The ViewModels and UI logic are already built to be
 * ID-agnostic, meaning they won't need to change even if the underlying
 * database starts managing hundreds of sessions.
 * 3. **Stable Identity**: By relying on [StopwatchLapModel.lapIndex] for domain identification,
 * we ensure that business logic remains consistent regardless of how
 * SQLite handles primary key increments.
 */
object StopwatchMapper {

    /**
     * Entry point for the Repository to transform database results into domain data.
     * * We use the DTO here because it encapsulates the relationship logic, allowing
     * the caller to get a complete model without knowing how the JOIN or Relation
     * was performed.
     */
    fun StopwatchWithLaps.toDomainModel(): StopwatchModel {
        return stopwatch.toDomainModel(laps)
    }

    /**
     * Prepares a Domain model for persistent storage.
     * * We re-attach the singleton ID (default 1) because the Domain layer is unaware
     * of primary keys, but SQLite requires them to prevent creating duplicate
     * stopwatch records on every save.
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
     * Transforms a raw state entity and its associated laps into a Domain model.
     * * IDs are discarded here to ensure that UI and Business layers rely on
     * stable domain properties (like timestamps) rather than ephemeral database
     * row identifiers.
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
     * Converts a domain lap into a persistence-ready entity.
     * * We hardcode the [StopwatchLapEntity.id] to 0 because Room interprets a zero/null value as a
     * signal to auto-generate a new unique primary key, simplifying the insertion
     * flow for the caller.
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
     * Transforms a database lap record back into a pure domain object.
     * * We remove the ID because the domain layer identifies laps by their
     * [StopwatchLapModel.lapIndex]. Using business-relevant indices instead of database keys
     * makes the UI logic more predictable and easier to test.
     */
    fun StopwatchLapEntity.toDomainModel(): StopwatchLapModel =
        StopwatchLapModel(
            lapIndex = lapIndex,
            lapStartTimeMillis = lapStartTimeMillis,
            lapElapsedTimeMillis = lapElapsedTimeMillis,
            lapEndTimeMillis = lapEndTimeMillis
        )

}