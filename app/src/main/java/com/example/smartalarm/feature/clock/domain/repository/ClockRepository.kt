package com.example.smartalarm.feature.clock.domain.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Repository contract for managing saved clock places.
 *
 * Provides domain-level operations for retrieving,
 * persisting, and removing user-saved locations.
 *
 * The implementation details (e.g., local database, caching strategy)
 * are abstracted away from consumers of this interface.
 */
interface ClockRepository {

    /**
     * Retrieves all saved places.
     *
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] if the operation fails.
     */
    suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError>

    /**
     * Persists the given [PlaceModel].
     *
     * @param placeModel The domain model to insert or update.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError>

    /**
     * Removes a saved place by its unique identifier.
     *
     * @param placeId The primary key of the place to delete.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError>
}