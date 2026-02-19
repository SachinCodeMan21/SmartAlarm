package com.example.smartalarm.feature.clock.data.repository

import com.example.smartalarm.feature.clock.data.datasource.contract.ClockLocalDataSource
import com.example.smartalarm.feature.clock.data.mapper.ClockMapper.toEntity
import com.example.smartalarm.feature.clock.data.mapper.ClockMapper.toModel
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import javax.inject.Inject


/**
 * Implementation of [ClockRepository] that uses a local data source to manage
 * clock-related data such as saved places and time zones.
 *
 * @property clockLocalDataSource The data source used for accessing local data.
 */
class ClockRepositoryImpl @Inject constructor(
    private val clockLocalDataSource: ClockLocalDataSource
) : ClockRepository {

    /**
     * Retrieves all saved places from the local data source and maps them to domain models.
     *
     * @return A [Result] containing a list of [PlaceModel] on success, or an error on failure.
     */
    override suspend fun getAllSavedPlaces(): Result<List<PlaceModel>> {
        return try {
            val places = clockLocalDataSource.getAllSavedTimeZones().map { it.toModel() }
            Result.Success(places)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Inserts a place into the local data source after mapping it to an entity.
     *
     * @param placeModel The place to insert.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun insertPlace(placeModel: PlaceModel): Result<Unit> {
        return try {
            clockLocalDataSource.insertTimeZone(placeModel.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Deletes a place from the local data source by its ID.
     *
     * @param placeId The ID of the place to delete.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun deletePlaceById(placeId: Long): Result<Unit> {
        return try {
            clockLocalDataSource.deleteTimeZoneById(placeId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}


