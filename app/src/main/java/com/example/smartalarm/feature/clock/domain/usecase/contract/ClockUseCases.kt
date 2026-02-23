package com.example.smartalarm.feature.clock.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.model.Result


interface ClockUseCases {
    suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError>
    suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError>
    suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError>
}



/**
 * Interface defining use cases related to clock and time zone operations.
 *
 * Functions:
 * 1. [getAllSavedPlaces] - Retrieves all saved places asynchronously.
 * 2. [insertPlace] - Inserts a new place asynchronously.
 * 3. [deletePlaceById] - Deletes a place by its ID asynchronously.
 */
//interface ClockUseCases {
//
//    /**
//     * Retrieves a list of all saved timezone places.
//     *
//     * @return [Result] wrapping a list of [PlaceModel] on success or an error on failure.
//     */
//    suspend fun getAllSavedPlaces(): Result<List<PlaceModel>>
//
//    /**
//     * Inserts a new place.
//     *
//     * @param placeModel The [PlaceModel] instance to insert.
//     * @return [Result] indicating success or failure.
//     */
//    suspend fun insertPlace(placeModel: PlaceModel): Result<Unit>
//
//    /**
//     * Deletes a place by its ID.
//     *
//     * @param placeId The ID of the place to delete.
//     * @return [Result] indicating success or failure.
//     */
//    suspend fun deletePlaceById(placeId: Long): Result<Unit>
//}
//
