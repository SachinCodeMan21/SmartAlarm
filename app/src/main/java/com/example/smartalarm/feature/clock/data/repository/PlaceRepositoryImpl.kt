package com.example.smartalarm.feature.clock.data.repository

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.extension.myRunCatchingResult
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.mapper.PlaceMapper.toEntity
import com.example.smartalarm.feature.clock.data.mapper.PlaceMapper.toModel
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import javax.inject.Inject


/**
 * Implementation of [PlaceRepository] using an **offline-first** data strategy.
 *
 * This repository coordinates between local and remote data sources to provide
 * fast, reliable, and consistent place search results.
 *
 * Strategy:
 * 1. Query the local database first for immediate results.
 * 2. If no local match is found, fetch data from the remote API.
 * 3. Cache remote results locally for future offline access.
 * 4. Re-query the local database to ensure consistency and return
 *    fully mapped domain models (including persisted fields such as IDs).
 *
 * This approach minimizes unnecessary network calls, enables offline capability,
 * and guarantees a single source of truth (the local database).
 *
 * All operations are wrapped using [myRunCatchingResult] to convert exceptions
 * into domain-level [DataError] results.
 *
 * @property localDataSource Provides access to local database operations.
 * @property remoteDataSource Provides access to remote place search APIs.
 */
class PlaceRepositoryImpl @Inject constructor(
    private val localDataSource: PlaceLocalDataSource,
    private val remoteDataSource: PlaceRemoteDataSource,
) : PlaceRepository {

    /**
     * Persists a [PlaceModel] to the local database.
     *
     * @param place The domain model to store.
     * @return [MyResult] indicating success or containing a [DataError].
     */
    override suspend fun savePlace(place: PlaceModel): MyResult<Unit, DataError> =
        myRunCatchingResult {
            localDataSource.insertPlace(place.toEntity())
        }

    /**
     * Searches for places matching the given [query] using offline-first logic.
     *
     * @param query The search keyword.
     * @return [MyResult] containing a list of [PlaceModel] on success,
     * or a [DataError] on failure.
     */
    override suspend fun searchPlaces(query: String): MyResult<List<PlaceModel>, DataError> =
        myRunCatchingResult {

            // 1. Attempt to retrieve cached results
            val localResults = localDataSource.searchPlace("%$query%")

            if (localResults.isNotEmpty()) {
                return@myRunCatchingResult localResults.map { it.toModel() }
            }

            // 2. Fetch from remote source
            val remoteDtos = remoteDataSource.searchPlaces(query)

            // 3. Cache results locally
            val entities = remoteDtos.map { it.toEntity() }
            localDataSource.insertAllPlaces(entities)

            // 4. Re-query local database to ensure consistency
            localDataSource
                .searchPlace("%$query%")
                .map { it.toModel() }
        }
}