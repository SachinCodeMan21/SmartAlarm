package com.example.smartalarm.feature.alarm.data.mapper

import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import com.example.smartalarm.feature.alarm.data.mapper.MissionMapper.toDomain
import com.example.smartalarm.feature.alarm.data.mapper.MissionMapper.toEntity
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel

/**
 * Utility object for mapping between database entities and domain models related to alarms.
 *
 * Provides extension functions to convert between:
 * - [AlarmWithMissions] (database representation combining alarm and missions)
 * - [AlarmModel] (domain model used in the application logic)
 * - [AlarmEntity] and [MissionEntity] (individual database entities)
 */
object AlarmMapper {

    /**
     * Converts an [AlarmWithMissions] database object to the domain-level [AlarmModel].
     *
     * Maps all fields including alarm details and associated missions.
     *
     * @receiver The [AlarmWithMissions] object containing alarm and its missions.
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
     * Converts an [AlarmModel] domain object to a pair of database entities:
     * an [AlarmEntity] and a list of associated [MissionEntity]s.
     *
     * This is used when persisting or updating alarm data in the database.
     *
     * @receiver The [AlarmModel] domain object.
     * @return A [Pair] containing the [AlarmEntity] and a list of [MissionEntity]s.
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
