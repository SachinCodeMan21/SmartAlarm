package com.example.smartalarm.feature.clock.data.mapper

import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity
import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Utility object for mapping between [ClockEntity] (database model) and [PlaceModel] (domain/UI model).
 *
 * These conversions are used when transforming data from the local Room database into a format
 * suitable for UI rendering, and vice versa when persisting user-selected time zones.
 */
object ClockMapper {

    /**
     * Converts a [ClockEntity] from the database into a [PlaceModel] used by the UI layer.
     *
     * @receiver The [ClockEntity] representing a saved time zone in the `clock_table`.
     * @return A [PlaceModel] with equivalent values, suitable for display and clock updates.
     */
    fun ClockEntity.toModel(): PlaceModel {
        return PlaceModel(
            id = id,
            fullName = fullName,
            primaryName = primaryName,
            timeZoneId = timeZoneId,
            offsetSeconds = offsetSeconds,
            currentTime = currentTime
        )
    }

    /**
     * Converts a [PlaceModel] from the domain/UI layer into a [ClockEntity] for local persistence.
     *
     * @receiver The [PlaceModel] representing a user-selected time zone.
     * @return A [ClockEntity] to be inserted or updated in the `clock_table`.
     */
    fun PlaceModel.toEntity(): ClockEntity {
        return ClockEntity(
            id = id,
            fullName = fullName,
            primaryName = primaryName,
            timeZoneId = timeZoneId,
            offsetSeconds = offsetSeconds,
            currentTime = currentTime
        )
    }

}
