package com.example.smartalarm.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartalarm.feature.alarm.data.local.converters.AlarmConverters
import com.example.smartalarm.feature.alarm.data.local.dao.AlarmDao
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.clock.data.local.dao.ClockDao
import com.example.smartalarm.feature.clock.data.local.dao.PlaceDao
import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity
import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopwatchDao
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.example.smartalarm.feature.timer.data.local.converter.TimerConverters
import com.example.smartalarm.feature.timer.data.local.dao.TimerDao
import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity

/**
 * Room database for managing alarm-related data in the app.
 *
 * This database provides access to the following entities:
 * - [StopwatchStateEntity]: Stopwatch session information & Operations.
 * - [StopwatchLapEntity]: Individual lap records for stopwatches.
 *
 * Version 1 with schema export disabled.
 */
@Database(
    entities = [
        AlarmEntity::class,
        MissionEntity::class,
        ClockEntity::class,
        PlaceEntity::class,
        TimerEntity::class,
        StopwatchStateEntity::class,
        StopwatchLapEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AlarmConverters::class, TimerConverters::class)
abstract class MyDatabase : RoomDatabase() {

    /** Provides access to alarm-related database operations. */
    abstract fun alarmsDao(): AlarmDao

    /** Provides access to clock-related database operations. */
    abstract fun clockDao(): ClockDao

    /** Provides access to place-related database operations. */
    abstract fun placeDao(): PlaceDao

    /** Provides access to timer-related database operations. */
    abstract fun timerDao(): TimerDao

    /** Provides access to stopwatch-related database operations. */
    abstract fun stopwatchDao(): StopwatchDao

}