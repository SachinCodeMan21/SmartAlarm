package com.example.smartalarm.feature.clock.data.datasource.contract

import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlaceLocalDataSource {

    /**
     * Returns all saved places as a flow.
     */
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    /**
     * Inserts or updates a place entity.
     */
    suspend fun insertPlace(place: PlaceEntity)

    /**
     * Inserts or updates a list of place entity.
     */
    suspend fun insertAllPlaces(placeEntities: List<PlaceEntity>)

    /**
     * Searches places by query.
     */
    suspend fun searchPlace(query: String): List<PlaceEntity>
}
