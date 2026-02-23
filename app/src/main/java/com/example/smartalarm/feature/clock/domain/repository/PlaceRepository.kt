package com.example.smartalarm.feature.clock.domain.repository

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.model.Result

interface PlaceRepository {

    /**
     * Inserts a [PlaceModel] into the local database.
     *
     * @param place The place to insert.
     * @return [MyResult.Success] if the insertion was successful,
     * or [MyResult.Error] containing [DataError.Local] if a database issue occurred.
     */
    suspend fun savePlace(place: PlaceModel): MyResult<Unit, DataError>

    /**
     * Searches for places either locally or remotely based on the query.
     *
     * Implementation strategy:
     * 1. First tries to search locally.
     * 2. If no results are found locally, falls back to remote search.
     * 3. Syncs remote results to local storage for offline-first persistence.
     *
     * @param query The search keyword entered by the user.
     * @return [MyResult.Success] with a list of [PlaceModel]s,
     * or [MyResult.Error] with [DataError.Network] or [DataError.Local].
     */
    suspend fun searchPlaces(query: String): MyResult<List<PlaceModel>, DataError>
}


/*
interface PlaceRepository {

    */
/**
     * Inserts a [PlaceModel] into the local database.
     *
     * @param place The place to insert.
     * @return A [Result] indicating the success or failure of the operation.
     *//*

    //suspend fun savePlace(place: PlaceModel): Result<Unit>
    suspend fun savePlace(place: PlaceModel): Result<Unit>


    */
/**
     * Searches for places either locally or remotely based on the query.
     *
     * 1. First tries to search locally.
     * 2. If no results are found locally, falls back to remote search.
     *
     * @param query The search keyword entered by the user.
     * @return A list of [PlaceModel]s either from the local database or fetched from the remote service.
     *//*

    suspend fun searchPlaces(query: String): Result<List<PlaceModel>>
}*/
