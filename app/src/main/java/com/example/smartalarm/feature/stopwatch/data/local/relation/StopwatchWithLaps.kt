package com.example.smartalarm.feature.stopwatch.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity

/**
 * A Data Transfer Object (DTO) representing a **One-to-Many** relationship.
 * * This class links a single [StopwatchStateEntity] to its multiple associated
 * [StopwatchLapEntity] records.
 * - This class is used by Room to perform a multi-table fetch in a single transaction.
 *
 * ### Relationship Details:
 * - **Parent**: [StopwatchStateEntity] (The 'One')
 * - **Children**: [StopwatchLapEntity] (The 'Many')
 * - **Join Key**: `stopwatch_state.id` <-> `stopwatch_laps.stopwatch_id`
 *
 * ### Behavioral Note:
 * Since this is a @Relation, Room fetches these in a non-atomic way unless the
 * calling DAO method is wrapped in a @Transaction.
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