package com.example.smartalarm.feature.clock.data.datasource.contract

import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlaceLocalDataSource {

    fun getAllPlaces(): Flow<List<PlaceEntity>>

    suspend fun insertPlace(place: PlaceEntity)

    suspend fun insertAllPlaces(placeEntities: List<PlaceEntity>)

    suspend fun searchPlace(query: String): List<PlaceEntity>
}
