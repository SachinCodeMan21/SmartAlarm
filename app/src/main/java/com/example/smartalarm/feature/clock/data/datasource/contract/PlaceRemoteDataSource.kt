package com.example.smartalarm.feature.clock.data.datasource.contract

import com.example.smartalarm.feature.clock.data.remote.dto.response.PlaceDto

/**
 * Defines contract for accessing place-related remote data from APIs such as Google Places and Time Zone APIs.
 */
interface PlaceRemoteDataSource {

    /**
     * Fetches places based on a search query by performing:
     * - Place autocomplete prediction
     * - Place detail lookup
     * - Time zone lookup
     *
     * Combines results into a list of [PlaceDto].
     *
     * @param query The search string entered by the user.
     * @return A list of [PlaceDto] representing structured place information.
     * @throws Exception if any network call or data extraction fails.
     */
    suspend fun searchPlaces(query: String): List<PlaceDto>
}
