package com.example.smartalarm.feature.alarm.data.mapper

import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType


/**
 * Provides mapping functions to convert between database entities ([MissionEntity])
 * and the domain model ([Mission]) used in the app.
 *
 * This object isolates the database layer from the domain layer,
 * allowing the app to work with clean, platform-independent models.
 */
object MissionMapper {

    /**
     * Converts a domain model [Mission] into a database entity [MissionEntity].
     *
     * Useful for persisting a mission in the local database and linking it to an alarm.
     *
     * @receiver The [Mission] to convert.
     * @param alarmId The ID of the parent alarm to associate this mission with.
     * @return The corresponding [MissionEntity] for database storage.
     */
    fun Mission.toEntity(alarmId: Int): MissionEntity {
        return MissionEntity(
            id = 0,
            alarmId = alarmId,
            type = type.getName().lowercase(),
            difficulty = difficulty.name,
            rounds = rounds,
            iconResId = iconResId,
            isCompleted = isCompleted
        )
    }

    /**
     * Converts a [MissionEntity] from the database into a domain model [Mission].
     *
     * Maps all properties including type and difficulty to their domain representations.
     *
     * @receiver The [MissionEntity] to convert.
     * @return The corresponding [Mission] domain object.
     */
    fun MissionEntity.toDomain(): Mission {
        return Mission(
            type = MissionType.fromName(type),
            difficulty = Difficulty.valueOf(difficulty),
            rounds = rounds,
            iconResId = iconResId,
            isCompleted = isCompleted
        )
    }
}