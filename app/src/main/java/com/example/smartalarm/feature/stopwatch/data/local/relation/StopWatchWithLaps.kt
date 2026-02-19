package com.example.smartalarm.feature.stopwatch.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity

/**
 * Represents the one-to-many relationship between a [StopWatchEntity] and its associated lap records.
 *
 * Used by Room to fetch a stopwatch session along with all its related lap entries in a single query.
 *
 * @property stopwatch The stopwatch session entity.
 * @property laps The list of lap records linked to the stopwatch, typically ordered by [StopWatchLapEntity.lapIndexId].
 */
data class StopWatchWithLaps(
    @Embedded val stopwatch: StopWatchEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId",
        entity = StopWatchLapEntity::class
    )
    val laps: List<StopWatchLapEntity>
)
