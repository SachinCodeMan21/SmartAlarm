package com.example.smartalarm.feature.clock.domain.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Repository contract for managing place-related data.
 *
 * Defines the operations available to the domain layer for
 * persisting and searching places, abstracting away the
 * underlying data sources (local or remote).
 */
interface PlaceRepository {

    /**
     * Persists a [PlaceModel].
     *
     * @param place The domain model to store.
     * @return [MyResult] indicating success, or a [DataError]
     * describing the failure.
     */
    suspend fun savePlace(place: PlaceModel): MyResult<Unit, DataError>

    /**
     * Searches for places matching the provided query.
     *
     * The repository guarantees that the returned data is
     * consistent with its configured data strategy
     * (e.g., cached, remote, or hybrid).
     *
     * @param query The search keyword.
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] if the operation fails.
     */
    suspend fun searchPlaces(query: String): MyResult<List<PlaceModel>, DataError>
}