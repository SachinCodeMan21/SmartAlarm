package com.example.smartalarm.feature.clock.data.datasource.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto

/**
 * Remote data source responsible for fetching place-related data from a network API.
 *
 * This interface defines the contract for searching places using a remote service.
 * Implementations are expected to handle network communication and map responses
 * into [PlaceDto] objects.
 */
interface PlaceRemoteDataSource {

    /**
     * Searches for places that match the given query string.
     *
     * @param query The search keyword or phrase used to find matching places.
     * @return A list of [PlaceDto] objects that match the search query.
     *
     * @throws Exception If a network error or API error occurs during the request.
     */
    //suspend fun searchPlaces(query: String): List<PlaceDto>
    suspend fun searchPlaces(query: String): MyResult<List<PlaceDto>, DataError>

}