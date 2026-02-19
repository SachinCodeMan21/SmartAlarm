package com.example.smartalarm.feature.timer.data.datasource.impl

import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.data.local.dao.TimerDao
import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of [TimerLocalDataSource] using Room via [TimerDao].
 *
 * Delegates actual data operations to the Room DAO. Keeps Room-specific logic out of the repository.
 *
 * @property timerDao The DAO used for performing Room operations on timer data.
 */
class TimerLocalDataSourceImpl @Inject constructor(
    private val timerDao: TimerDao
) : TimerLocalDataSource {

    override fun getTimerList(): Flow<List<TimerEntity>> = timerDao.getTimerList()

    override suspend fun saveTimer(timerEntity: TimerEntity) {
        timerDao.saveTimer(timerEntity)
    }

    override suspend fun deleteTimerById(timerId: Int) {
        timerDao.deleteTimerById(timerId)
    }
}
