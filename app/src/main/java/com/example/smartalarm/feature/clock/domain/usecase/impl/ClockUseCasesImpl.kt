package com.example.smartalarm.feature.clock.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.core.model.Result
import javax.inject.Inject


class ClockUseCasesImpl @Inject constructor(
    private val clockRepository: ClockRepository
) : ClockUseCases {

    override suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError> {
        return clockRepository.getAllSavedPlaces()
    }

    override suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError> {
        return clockRepository.insertPlace(placeModel)
    }

    override suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError> {
        return clockRepository.deletePlaceById(placeId)
    }
}

/**
 * Implementation of [ClockUseCases] that delegates calls to the [ClockRepository].
 *
 * @property clockRepository Repository handling data operations related to clock/time zones.
 */
//class ClockUseCasesImpl @Inject constructor(
//    private val clockRepository: ClockRepository
//) : ClockUseCases {
//
//    /**
//     * Retrieves all saved places by delegating to the repository.
//     *
//     * @return [Result] wrapping a list of [PlaceModel] on success or an error on failure.
//     */
//    override suspend fun getAllSavedPlaces(): Result<List<PlaceModel>> {
//        return clockRepository.getAllSavedPlaces()
//    }
//
//    /**
//     * Inserts a new place by delegating to the repository.
//     *
//     * @param placeModel The [PlaceModel] to insert.
//     * @return [Result] indicating success or failure.
//     */
//    override suspend fun insertPlace(placeModel: PlaceModel): Result<Unit> {
//        return clockRepository.insertPlace(placeModel)
//    }
//
//    /**
//     * Deletes a place by its ID by delegating to the repository.
//     *
//     * @param placeId The ID of the place to delete.
//     * @return [Result] indicating success or failure.
//     */
//    override suspend fun deletePlaceById(placeId: Long): Result<Unit> {
//        return clockRepository.deletePlaceById(placeId)
//    }
//}
