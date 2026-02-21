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
 * Repository implementation for managing place data using an **offline-first** approach.
 *
 * This class ensures that the app can provide fast, reliable search results even when
 * the user is offline, while still fetching up-to-date information when connected.
 *
 * Key responsibilities:
 * 1. **Offline-first search** – checks the local database first to quickly return cached results.
 * 2. **Remote fallback** – fetches from the GeoApify API if no local matches are found.
 * 3. **Cache update** – saves remote results to the local database for future offline access.
 * 4. **Consistency guarantee** – queries the local DB again to ensure returned results are complete
 *    and include enriched timezone information.
 *
 * This design improves user experience by minimizing network calls, providing instant
 * search feedback, and always showing accurate local times for places.
 *
 * @property localDataSource Handles reading and writing place data to the local database.
 * @property remoteDataSource Handles fetching place data and timezone information from the network.
 */
class PlaceRepositoryImpl @Inject constructor(
    private val localDataSource: PlaceLocalDataSource,
    private val remoteDataSource: PlaceRemoteDataSource,
) : PlaceRepository {

    /**
     * Saves a [place] to the local database.
     *
     * @param place The [PlaceModel] to save.
     * @return [Result.Success] if saved successfully, or [Result.Error] on failure.
     */
    override suspend fun savePlace(place: PlaceModel): Result<Unit> {
        return try {
            localDataSource.insertPlace(place.toEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(Exception())
        }
    }

    /**
     * Searches for places matching the given [query] using **offline-first logic**.
     *
     * The search flow:
     * 1. **Local check** – returns cached results if available, providing instant feedback.
     * 2. **Remote fetch** – if local results are empty, fetches from the API including timezone info.
     * 3. **Cache update** – inserts remote results into the local database for future offline queries.
     * 4. **Consistency check** – queries local DB again to return a complete, consistent result set.
     *
     * This approach ensures users always get fast, reliable results with accurate timezone
     * information, even without network connectivity.
     *
     * @param query The user's search keyword or phrase.
     * @return [Result.Success] with a list of [PlaceModel] or [Result.Error] if something goes wrong.
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
