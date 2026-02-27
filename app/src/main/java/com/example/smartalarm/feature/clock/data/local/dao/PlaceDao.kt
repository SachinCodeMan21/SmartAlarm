package com.example.smartalarm.feature.clock.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) responsible for managing access to
 * place-related data stored in the local Room database.
 *
 * Provides methods for querying, inserting, updating, and searching
 * cached place entities.
 */
@Dao
interface PlaceDao {

    /**
     * Returns a reactive stream of all stored places.
     *
     * The returned [Flow] automatically emits updates whenever the
     * underlying table data changes.
     *
     * @return A [Flow] emitting the current list of [PlaceEntity].
     */
    @Query("SELECT * FROM searched_places_table")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    /**
     * Inserts the given [PlaceEntity] into the database.
     *
     * If a record with the same primary key already exists,
     * it will be updated.
     *
     * @param place The entity to insert or update.
     */
    @Upsert
    suspend fun insertPlace(place: PlaceEntity)

    /**
     * Inserts or updates a collection of [PlaceEntity] objects.
     *
     * Each entity is processed individually. Existing records
     * (based on primary key) will be updated.
     *
     * @param places List of entities to insert or update.
     */
    @Upsert
    suspend fun insertAllPlaces(places: List<PlaceEntity>)

    /**
     * Searches for places whose `full_name` or `primary_name`
     * matches the provided query pattern.
     *
     * The query string should include SQL wildcard characters
     * (e.g., `%keyword%`) if partial matching is desired.
     *
     * @param query SQL LIKE pattern used for matching.
     * @return List of matching [PlaceEntity] results.
     */
    @Query("SELECT * FROM searched_places_table WHERE full_name LIKE :query OR primary_name LIKE :query")
    suspend fun searchPlace(query: String): List<PlaceEntity>
}

