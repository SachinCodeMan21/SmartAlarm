package com.example.smartalarm.feature.clock.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for accessing and modifying place-related data in the local Room database.
 */
@Dao
interface PlaceDao {

    /**
     * Retrieves all saved places from the local database as a reactive stream.
     *
     * @return A [Flow] that emits the current list of [PlaceEntity] objects.
     */
    @Query("SELECT * FROM searched_places_table")
    fun getAllPlaces(): Flow<List<PlaceEntity>>



    /**
     * Inserts a new place or updates it if it already exists (based on primary key).
     *
     * @param place The [PlaceEntity] to insert or update.
     */
    @Upsert
    suspend fun insertPlace(place: PlaceEntity)


    /**
     * Inserts or updates a list of places in the local database.
     *
     * Each place in the list will be inserted as a separate row.
     * If a place with the same primary key already exists, it will be updated (replaced) instead of inserted again.
     *
     * This operation helps keep the local cache in sync with the latest data from the remote source.
     *
     * @param places List of [PlaceEntity] objects to be inserted or updated.
     */
    @Upsert
    suspend fun insertAllPlaces(places: List<PlaceEntity>)


    /**
     * Searches the local database for places where either the full name or primary name matches the given query.
     *
     * @param query The search string to match against `full_name` or `primary_name`. Should include SQL wildcards (e.g., `%text%`).
     * @return A list of [PlaceEntity] objects that match the query.
     */
    @Query("SELECT * FROM searched_places_table WHERE full_name LIKE :query OR primary_name LIKE :query")
    suspend fun searchPlace(query: String): List<PlaceEntity>

}

