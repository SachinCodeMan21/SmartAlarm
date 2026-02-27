package com.example.smartalarm.feature.clock.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Use case contract for place search operations.
 *
 * Defines business-level actions related to searching
 * and saving places. This abstraction ensures the
 * presentation layer does not depend directly on repositories.
 */
interface PlaceSearchUseCases {

    /**
     * Retrieves place predictions based on the provided query.
     *
     * @param query The search keyword.
     * @return [MyResult] containing a list of matching [PlaceModel]s
     * on success, or a [DataError] on failure.
     */
    suspend fun getPlacePredictions(query: String): MyResult<List<PlaceModel>, DataError>

    /**
     * Persists the selected place.
     *
     * @param placeInfo The place to save.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    suspend fun savePlace(placeInfo: PlaceModel): MyResult<Unit, DataError>
}