package com.example.smartalarm.feature.clock.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Default implementation of [PlaceSearchUseCases].
 *
 * Coordinates place search and persistence operations by delegating
 * to the underlying [PlaceRepository].
 *
 * Responsibilities:
 * - Validates search input before triggering data operations.
 * - Applies a debounce delay to reduce rapid consecutive searches.
 * - Delegates data retrieval and persistence to the repository layer.
 *
 * This class contains business-level logic and remains independent
 * of presentation and data-layer implementation details.
 */
class PlaceSearchUseCasesImpl @Inject constructor(
    private val repository: PlaceRepository
) : PlaceSearchUseCases {

    companion object {
        private const val DEBOUNCE_DELAY = 500L
    }

    /**
     * Returns place predictions matching the provided query.
     *
     * If the query is blank, an empty successful result is returned
     * without invoking the repository.
     *
     * @param query The user-provided search term.
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] if the operation fails.
     */
    override suspend fun getPlacePredictions(query: String): MyResult<List<PlaceModel>, DataError> {
        if (query.isBlank()) return MyResult.Success(emptyList())

        delay(DEBOUNCE_DELAY)

        return repository.searchPlaces(query)
    }

    /**
     * Persists the given [PlaceModel].
     *
     * @param placeInfo The domain model to store.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun savePlace(placeInfo: PlaceModel): MyResult<Unit, DataError> {
        return repository.savePlace(placeInfo)
    }

}