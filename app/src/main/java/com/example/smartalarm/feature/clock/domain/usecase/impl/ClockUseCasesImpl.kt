package com.example.smartalarm.feature.clock.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import javax.inject.Inject


/**
 * Default implementation of [ClockUseCases].
 *
 * Provides business-level operations for managing
 * user-saved clock places by delegating to [ClockRepository].
 *
 * This layer isolates the presentation layer from direct
 * repository dependencies and keeps business rules centralized.
 */
class ClockUseCasesImpl @Inject constructor(
    private val clockRepository: ClockRepository
) : ClockUseCases {

    /**
     * Retrieves all the user selected saved places.
     *
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] on failure.
     */
    override suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError> {
        return clockRepository.getAllSavedPlaces()
    }

    /**
     * Saves or updates a place in the user's saved places .
     *
     * @param placeModel The domain model to persist.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError> {
        return clockRepository.insertPlace(placeModel)
    }

    /**
     * Deletes a saved place by its identifier.
     *
     * @param placeId The unique ID of the place.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError> {
        return clockRepository.deletePlaceById(placeId)
    }
}