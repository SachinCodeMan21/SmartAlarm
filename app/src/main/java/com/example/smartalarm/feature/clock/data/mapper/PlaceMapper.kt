package com.example.smartalarm.feature.clock.data.mapper

import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto
import com.example.smartalarm.feature.clock.domain.model.PlaceModel


/**
 * Object responsible for mapping between different models related to time zone places.
 *
 * Responsibilities:
 * - Converts between [PlaceEntity] (used for local Room database storage) and [PlaceModel] (used in the domain/UI layer).
 * - Converts [PlaceDto] (retrieved from remote APIs) into [PlaceEntity] for local caching.
 *
 * This mapper helps separate concerns between data, domain, and presentation layers,
 * promoting clean architecture and testability.
 */
object PlaceMapper {

    /**
     * Converts a [PlaceEntity] to a [PlaceModel].
     *
     * @receiver The [PlaceEntity] to convert.
     * @return Corresponding [PlaceModel].
     */
    fun PlaceEntity.toModel(): PlaceModel = PlaceModel(
        id = id,
        fullName = fullName,
        primaryName = primaryName,
        timeZoneId = timeZoneId,
        offsetSeconds = offsetSeconds,
        currentTime = currentTime
    )

    /**
     * Converts a [PlaceModel] to a [PlaceEntity].
     *
     * @receiver The [PlaceModel] to convert.
     * @return Corresponding [PlaceEntity].
     */
    fun PlaceModel.toEntity(): PlaceEntity = PlaceEntity(
        id = id,
        fullName = fullName,
        primaryName = primaryName,
        timeZoneId = timeZoneId,
        offsetSeconds = offsetSeconds,
        currentTime = currentTime
    )

    /**
     * Maps a [PlaceDto] to a [PlaceEntity] for insertion into the local database.
     *
     * This transformation is used in the repository layer to convert remote data into local persistence models.
     *
     * @receiver The [PlaceDto] to convert.
     * @return A [PlaceEntity] containing equivalent data for Room storage.
     */
    fun PlaceDto.toEntity(): PlaceEntity {
        return PlaceEntity(
            fullName = this.fullName,
            primaryName = this.primaryName,
            timeZoneId = this.timeZoneId,
            offsetSeconds = this.offsetSeconds,
            currentTime = this.currentTime
        )
    }
}
