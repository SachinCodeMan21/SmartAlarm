package com.example.smartalarm.feature.clock.data.remote.api

import com.example.smartalarm.feature.clock.data.remote.dto.GeoApifyResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for interacting with the GeoApify API.
 *
 * Provides endpoints for:
 * Autocomplete place predictions based on user input.
 */
interface GeoApifyApiService {

    /**
     * Retrieves a list of place predictions for the given [query] text.
     *
     * Typically used for autocomplete functionality while the user is typing.
     *
     * @param query The input text to search for matching places (e.g., "Mumbai").
     * @param lang Optional language code for the results. Defaults to `"en"`.
     * @param limit Maximum number of results to return. Defaults to 5.
     * @return A [GeoApifyResponse] containing the matching places in a `features` list.
     */
    @GET("v1/geocode/autocomplete")
    suspend fun getPlacePredictions(
        @Query("text") query: String,
        @Query("lang") lang: String = "en",
        @Query("limit") limit: Int = 5
    ): GeoApifyResponse

}