package com.example.smartalarm.feature.clock.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [PlaceSearchUseCases] that coordinates data operations related to
 * place searching and management through the [PlaceRepository].
 */
class PlaceSearchUseCasesImpl @Inject constructor(
    private val repository: PlaceRepository
) : PlaceSearchUseCases {

    /**
     * Searches for place predictions based on the user's query.
     *
     * @param query The user-entered search query.
     * @return [MyResult.Success] with results, or [MyResult.Error] with [DataError].
     */
    override suspend fun getPlacePredictions(query: String): MyResult<List<PlaceModel>, DataError> {
        // Validation logic: If query is blank, return empty success immediately
        // without hitting the repository or network.
        if (query.isBlank()) return MyResult.Success(emptyList())

        return repository.searchPlaces(query)
    }

    /**
     * Inserts or updates a place in the local database.
     *
     * @param placeInfo The [PlaceModel] to be saved.
     * @return [MyResult.Success] on success, or [MyResult.Error] on database failure.
     */
    override suspend fun savePlace(placeInfo: PlaceModel): MyResult<Unit, DataError> {
        return repository.savePlace(placeInfo)
    }
}

///**
// * Implementation of [PlaceSearchUseCases] that coordinates data operations related to
// * place searching and management through the [PlaceRepository].
// *
// * This class contains business logic to:
// * - Fetch all saved places as a flow.
// * - Search for places locally or remotely.
// * - Save places to the local database.
// *
// * @property repository The repository responsible for accessing place data from local and remote sources.
// */
//class PlaceSearchUseCasesImpl @Inject constructor(
//    private val repository: PlaceRepository
//) : PlaceSearchUseCases {
//
//    /**
//     * Returns a reactive stream of all places stored in the local database.
//     *
//     * @return A [Flow] emitting the latest list of [PlaceModel]s as the data changes.
//     */
////    override fun getAllPlaces(): Flow<List<PlaceModel>> {
////        return repository.getAllPlaces()
////    }
//
//    /**
//     * Searches for place predictions based on the user's query.
//     *
//     * If the query is blank, an empty result is returned.
//     * Otherwise, the local data source is searched first.
//     * If no results are found locally, a remote fetch is triggered, and results are cached locally.
//     *
//     * @param query The user-entered search query.
//     * @return A [Result] containing a list of [PlaceModel]s or an error if the operation fails.
//     */
//    override suspend fun getPlacePredictions(query: String): Result<List<PlaceModel>> {
//        if (query.isBlank()) return Result.Success(emptyList())
//        return repository.searchPlaces(query)
//    }
//
//    /**
//     * Inserts or updates a place in the local database.
//     *
//     * @param placeInfo The [PlaceModel] to be saved.
//     * @return A [Result] indicating whether the operation was successful or if an error occurred.
//     */
//    override suspend fun savePlace(placeInfo: PlaceModel): Result<Unit> {
//        return repository.savePlace(placeInfo)
//    }
//}
