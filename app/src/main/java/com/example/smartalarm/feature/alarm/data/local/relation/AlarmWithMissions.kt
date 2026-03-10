package com.example.smartalarm.feature.alarm.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity


/**
 * Represents an alarm along with its associated missions.
 *
 * This class is used to model a parent-child relationship between an `AlarmEntity`
 * and its related `MissionEntity` objects in Room.
 *
 * The `@Embedded` annotation includes the parent alarm fields directly in this object,
 * while the `@Relation` annotation tells Room to automatically fetch all child missions
 * linked to this alarm via the `alarmId` foreign key.
 *
 * Use this class when you need to display or process an alarm together with
 * all its missions in a single object, for example in the UI or in business logic.
 *
 * @property alarm The parent [AlarmEntity] containing the alarm details.
 * @property missions A list of [MissionEntity] objects associated with this alarm.
 */
data class AlarmWithMissions(
    @Embedded val alarm: AlarmEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "alarmId"
    )
    val missions: List<MissionEntity>
)