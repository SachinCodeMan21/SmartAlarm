package com.example.smartalarm.feature.alarm.data.mapper

import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import com.example.smartalarm.feature.alarm.data.mapper.MissionMapper.toDomain
import com.example.smartalarm.feature.alarm.data.mapper.MissionMapper.toEntity
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel


/**
 * Provides mapping functions to convert between database entities ([AlarmEntity], [MissionEntity])
 * and the domain model ([AlarmModel]) used in the app.
 *
 * This object helps isolate the database layer from the domain layer,
 * allowing the app to work with clean, platform-independent models.
 */
object AlarmMapper {

    /**
     * Converts an [AlarmWithMissions] (Room entity + relation) to a domain model [AlarmModel].
     *
     * Maps the alarm entity and its associated mission entities to their corresponding domain representations.
     *
     * @receiver The [AlarmWithMissions] to convert.
     * @return The corresponding [AlarmModel] domain object.
     */
    fun AlarmWithMissions.toDomainModel(): AlarmModel = AlarmModel(
        id = alarm.id,
        label = alarm.label,
        time = alarm.time,
        isDailyAlarm = alarm.isDailyAlarm,
        days = alarm.days,
        missions = missions.map { it.toDomain() },
        volume = alarm.volume,
        isVibrateEnabled = alarm.isVibrateEnabled,
        alarmSound = alarm.alarmSound,
        snoozeSettings = alarm.snoozeSettings,
        isEnabled = alarm.isEnabled,
        alarmState = AlarmState.valueOf(alarm.alarmState)
    )

    /**
     * Converts a domain model [AlarmModel] into a database entity ([AlarmEntity])
     * along with its associated mission entities ([MissionEntity]).
     *
     * Useful for persisting an alarm and its missions in the local database.
     *
     * @receiver The [AlarmModel] to convert.
     * @return A [Pair] containing the [AlarmEntity] and a list of corresponding [MissionEntity]s.
     */
    fun AlarmModel.toEntityWithMissions(): Pair<AlarmEntity, List<MissionEntity>> {
        val alarmEntity = AlarmEntity(
            id = id,
            label = label,
            time = time,
            isDailyAlarm = isDailyAlarm,
            days = days,
            volume = volume,
            isVibrateEnabled = isVibrateEnabled,
            alarmSound = alarmSound,
            snoozeSettings = snoozeSettings,
            isEnabled = isEnabled,
            alarmState = alarmState.toString()
        )

        val missionEntities = missions.map { it.toEntity(alarmId = id) }
        return Pair(alarmEntity, missionEntities)
    }
}