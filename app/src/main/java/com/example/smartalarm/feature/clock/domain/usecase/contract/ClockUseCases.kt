package com.example.smartalarm.feature.clock.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel


/**
 * Use case contract for managing user-saved clock places.
 *
 * Encapsulates business operations related to retrieving,
 * persisting, and removing saved locations.
 *
 * This layer coordinates domain logic and acts as an
 * intermediary between the presentation layer and repositories.
 */
interface ClockUseCases {

    /**
     * Retrieves all saved places.
     *
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] describing the failure.
     */
    suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError>

    /**
     * Saves or updates a [PlaceModel].
     *
     * @param placeModel The domain model to persist.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError>

    /**
     * Deletes a saved place by its identifier.
     *
     * @param placeId The unique ID of the place to remove.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError>
}