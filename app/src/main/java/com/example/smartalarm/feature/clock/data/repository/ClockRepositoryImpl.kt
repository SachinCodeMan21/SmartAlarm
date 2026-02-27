package com.example.smartalarm.feature.clock.data.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.clock.data.datasource.contract.ClockLocalDataSource
import com.example.smartalarm.feature.clock.data.mapper.ClockMapper.toEntity
import com.example.smartalarm.feature.clock.data.mapper.ClockMapper.toModel
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import javax.inject.Inject

/**
 * Implementation of [ClockRepository] responsible for handling
 * clock-related data operations.
 *
 * This repository acts as an abstraction layer between the domain
 * and the local data source. It maps data entities to domain models
 * and wraps results using [MyResult] for unified success/error handling.
 *
 * All operations are executed safely using [myRunCatchingResult]
 * to convert exceptions into [DataError].
 *
 * @property clockLocalDataSource The local data source providing
 * access to Room database operations.
 */
class ClockRepositoryImpl @Inject constructor(
    private val clockLocalDataSource: ClockLocalDataSource
) : ClockRepository {

    /**
     * Retrieves all saved clock places from the local data source.
     *
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] on failure.
     */
    override suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError> =
        myRunCatchingResult {
            clockLocalDataSource
                .getAllSavedTimeZones()
                .map { it.toModel() }
        }

    /**
     * Inserts or updates a clock place in the local database.
     *
     * @param placeModel The domain model to persist.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError> =
        myRunCatchingResult {
            clockLocalDataSource.insertTimeZone(placeModel.toEntity())
        }

    /**
     * Deletes a saved place by its unique identifier.
     *
     * @param placeId The primary key of the place to remove.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError> =
        myRunCatchingResult {
            clockLocalDataSource.deleteTimeZoneById(placeId)
        }
}