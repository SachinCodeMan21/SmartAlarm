package com.example.smartalarm.feature.alarm.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity


/**
 * Represents a combined data structure of an alarm with its associated missions.
 *
 * Useful for retrieving an alarm and its missions in a single query using Room's
 * @Relation annotation.
 *
 * @property alarm The [AlarmEntity] instance.
 * @property missions The list of [MissionEntity] instances associated with the alarm.
 */
data class AlarmWithMissions(
    @Embedded val alarm: AlarmEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "alarmId"
    )
    val missions: List<MissionEntity>
)