package com.example.smartalarm.feature.clock.domain.repository

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel



interface ClockRepository {
    suspend fun getAllSavedPlaces(): MyResult<List<PlaceModel>, DataError>
    suspend fun insertPlace(placeModel: PlaceModel): MyResult<Unit, DataError>
    suspend fun deletePlaceById(placeId: Long): MyResult<Unit, DataError>
}



/**
 * Repository interface for managing clock-related data operations.
 */
//interface ClockRepository {
//
//    /**
//     * Retrieves all saved places.
//     *
//     * @return A [Result] containing a list of [PlaceModel] on success, or an error on failure.
//     */
//    suspend fun getAllSavedPlaces(): Result<List<PlaceModel>>
//
//    /**
//     * Inserts a new place into the data source.
//     *
//     * @param placeModel The [PlaceModel] to insert.
//     * @return A [Result] indicating success or failure.
//     */
//    suspend fun insertPlace(placeModel: PlaceModel): Result<Unit>
//
//    /**
//     * Deletes a place identified by its ID.
//     *
//     * @param placeId The ID of the place to delete.
//     * @return A [Result] indicating success or failure.
//     */
//    suspend fun deletePlaceById(placeId: Long): Result<Unit>
//}
