package com.example.smartalarm.feature.clock.domain.usecase.contract

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Defines the core use cases related to place searching and local place management.
 *
 * Acts as an abstraction layer between the UI (or ViewModel) and the data repositories,
 * encapsulating business logic for searching, retrieving, and saving places.
 */
interface PlaceSearchUseCases {

    /**
     * Retrieves a continuous stream of all places stored locally.
     *
     * @return A [Flow] emitting updated lists of [PlaceModel]s whenever the local database changes.
     */
    //fun getAllPlaces(): Flow<List<PlaceModel>>

    /**
     * Fetches place predictions based on the user's query.
     *
     * Behavior:
     * - If the query is blank, returns an empty list.
     * - Checks the local database for matches.
     * - If no local matches are found, fetches from the remote API and stores results locally.
     *
     * @param query The search string entered by the user.
     * @return A [Result] containing a list of matching [PlaceModel]s on success, or an error on failure.
     */
    suspend fun getPlacePredictions(query: String): Result<List<PlaceModel>>

    /**
     * Saves the selected place to the local database.
     * If the place already exists (based on primary key), it will be updated.
     *
     * @param placeInfo The [PlaceModel] representing the place to save.
     * @return A [Result] indicating success or an error if the operation fails.
     */
    suspend fun savePlace(placeInfo: PlaceModel): Result<Unit>
}
