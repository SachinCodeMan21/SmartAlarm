package com.example.smartalarm.feature.clock.data.repository

import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.mapper.PlaceMapper.toEntity
import com.example.smartalarm.feature.clock.data.mapper.PlaceMapper.toModel
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.extension.runCatchingResult
import javax.inject.Inject

/**
 * Repository implementation responsible for managing place data.
 * It follows a local-first strategy: attempts to retrieve data from the local database,
 * and falls back to remote API calls if not available.
 *
 * This class bridges the domain layer with both local and remote data sources.
 *
 * @property localDataSource Interface for local Room database access.
 * @property remoteDataSource Interface for accessing Google Places & Time Zone APIs.
 */
class PlaceRepositoryImpl @Inject constructor(
    private val localDataSource: PlaceLocalDataSource,
    private val remoteDataSource: PlaceRemoteDataSource,
) : PlaceRepository {

    /**
     * Saves a single place to the local database.
     *
     * @param place A [PlaceModel] representing the place to store.
     * @return [Result.Success] if insertion succeeds, or [Result.Error] on failure.
     */
    override suspend fun savePlace(place: PlaceModel): Result<Unit> = runCatchingResult {
        localDataSource.insertPlace(place.toEntity())
    }


    /**
     * Searches for places matching the given query.
     *
     * This function implements an offline-first strategy:
     * 1. Attempts to find matching places from the local database.
     * 2. If no local results are found, fetches matching places from the remote data source.
     * 3. Maps the remote data transfer objects (DTOs) to local entities and inserts them in bulk into the local database.
     * 4. Queries the local database again to return the updated, consistent list of places.
     *
     * This approach ensures that remote data is cached locally for future queries, minimizing network calls.
     *
     * @param query The search string input by the user.
     * @return A [Result] wrapping a list of [PlaceModel] on success, or a [Result.Error] if an exception occurs.
     */
    override suspend fun searchPlaces(query: String): Result<List<PlaceModel>> {

        return try {

            // Step 1: Check local
            val local = localDataSource.searchPlace("%$query%")
            if (local.isNotEmpty()) {
                return Result.Success(local.map { it.toModel() })
            }

            // Step 2: Fetch from remote
            val remoteDtos = remoteDataSource.searchPlaces(query)
            val placeEntities = remoteDtos.map { it.toEntity() }

            // Step 3: Insert all timezone at once
            localDataSource.insertAllPlaces(placeEntities)

            // Step 4: Query again from local to ensure consistency
            val updatedLocal = localDataSource.searchPlace("%$query%")
            Result.Success(updatedLocal.map { it.toModel() })

        } catch (e: Exception) {
            Result.Error(e)
        }

    }

}
