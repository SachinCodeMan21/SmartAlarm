package com.example.smartalarm.feature.clock.data.remote.api

import com.example.smartalarm.BuildConfig
import com.example.smartalarm.feature.clock.data.remote.dto.googlePlaces.PlacesResponse
import com.example.smartalarm.feature.clock.data.remote.dto.placeDetails.PlaceDetailsResponse
import com.example.smartalarm.feature.clock.data.remote.dto.timezone.TimeZoneResponse
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Retrofit service interface for accessing Google Places and Time Zone APIs.
 *
 * Responsibilities:
 * 1. Fetches place predictions based on user input (autocomplete feature).
 * 2. Retrieves detailed information about a selected place, including geographic coordinates.
 * 3. Obtains the time zone information for a given location and timestamp.
 *
 * All methods are suspend functions and must be called from a coroutine scope.
 */
interface GoogleApiService {
    @GET("place/autocomplete/json")
    suspend fun getPlacePredictions(
        @Query("input") query: String,
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY
    ): PlacesResponse

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "geometry/location",
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY
    ): PlaceDetailsResponse

    @GET("timezone/json")
    suspend fun getTimeZone(
        @Query("location") location: String,
        @Query("timestamp") timestamp: Long,
        @Query("key") apiKey: String = BuildConfig.GOOGLE_API_KEY
    ): TimeZoneResponse

}
