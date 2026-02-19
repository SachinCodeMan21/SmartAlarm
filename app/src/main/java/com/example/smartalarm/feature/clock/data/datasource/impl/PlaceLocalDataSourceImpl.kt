package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.local.dao.PlaceDao
import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [PlaceLocalDataSource] that interacts with the local database via [PlaceDao].
 *
 * @property placeDao DAO for accessing place data in the local database.
 */
class PlaceLocalDataSourceImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceLocalDataSource {

    /**
     * Retrieves all stored places as a reactive [Flow] of a list of [PlaceEntity].
     *
     * @return A [Flow] emitting lists of all place entities stored locally.
     */
    override fun getAllPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    /**
     * Inserts a single [PlaceEntity] into the local database.
     *
     * @param place The place entity to insert.
     */
    override suspend fun insertPlace(place: PlaceEntity) = placeDao.insertPlace(place)

    /**
     * Inserts multiple [PlaceEntity] objects into the local database in a single batch.
     *
     * @param placeEntities List of place entities to insert.
     */
    override suspend fun insertAllPlaces(placeEntities: List<PlaceEntity>) = placeDao.insertAllPlaces(placeEntities)

    /**
     * Searches for place entities matching the provided query string.
     *
     * @param query The search string to filter place entities.
     * @return A list of place entities matching the query.
     */
    override suspend fun searchPlace(query: String): List<PlaceEntity> = placeDao.searchPlace(query)
}
