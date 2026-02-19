package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.feature.clock.data.datasource.contract.ClockLocalDataSource
import com.example.smartalarm.feature.clock.data.local.dao.ClockDao
import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity
import javax.inject.Inject

/**
 * Implementation of [ClockLocalDataSource] that interacts with the Room database
 * using [ClockDao] for local persistence operations.
 *
 * @property clockDao The DAO used for database access.
 */
class ClockLocalDataSourceImpl @Inject constructor(
    private val clockDao: ClockDao
) : ClockLocalDataSource {

    /**
     * Retrieves all saved time zones from the local database.
     *
     * @return A list of [ClockEntity] objects.
     */
    override suspend fun getAllSavedTimeZones(): List<ClockEntity> = clockDao.getAllSavedTimeZones()

    /**
     * Inserts a time zone into the local database.
     *
     * @param entity The [ClockEntity] to insert.
     */
    override suspend fun insertTimeZone(entity: ClockEntity) = clockDao.insertTimeZone(entity)

    /**
     * Deletes a time zone from the local database by its ID.
     *
     * @param id The ID of the time zone to delete.
     */
    override suspend fun deleteTimeZoneById(id: Long) = clockDao.deleteTimeZoneById(id)
}
