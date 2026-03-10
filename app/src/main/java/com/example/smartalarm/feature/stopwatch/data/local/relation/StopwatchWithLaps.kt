package com.example.smartalarm.feature.stopwatch.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity

/**
 * Represents a Room relation between a stopwatch state and its associated laps.
 *
 * This data class models a one-to-many relationship where a single
 * [StopwatchStateEntity] can contain multiple [StopwatchLapEntity] records.
 * It allows Room to fetch the stopwatch state together with all recorded
 * laps in a single query.
 *
 * Relationship mapping:
 * - Parent: [StopwatchStateEntity]
 * - Child: [StopwatchLapEntity]
 * - Join key: `stopwatch_state.id` → `stopwatch_laps.stopwatch_id`
 *
 * Note:
 * DAO methods returning this relation should be annotated with `@Transaction`
 * to ensure the parent and child queries are executed atomically.
 */
data class StopwatchWithLaps(

    @Embedded
    val stopwatch: StopwatchStateEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatch_id",
        entity = StopwatchLapEntity::class
    )
    val laps: List<StopwatchLapEntity>
)