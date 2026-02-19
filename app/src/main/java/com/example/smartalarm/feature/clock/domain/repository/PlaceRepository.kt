package com.example.smartalarm.feature.clock.domain.repository

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    /**
     * Inserts a [PlaceModel] into the local database.
     *
     * @param place The place to insert.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun savePlace(place: PlaceModel): Result<Unit>

    /**
     * Searches for places either locally or remotely based on the query.
     *
     * 1. First tries to search locally.
     * 2. If no results are found locally, falls back to remote search.
     *
     * @param query The search keyword entered by the user.
     * @return A list of [PlaceModel]s either from the local database or fetched from the remote service.
     */
    suspend fun searchPlaces(query: String): Result<List<PlaceModel>>
}