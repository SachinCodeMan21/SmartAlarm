package com.example.smartalarm.core.di.modules

import android.content.Context
import androidx.room.Room
import com.example.smartalarm.core.database.MyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides the MyDatabase instance as a singleton.
 *
 * This module is installed in the [dagger.hilt.components.SingletonComponent], meaning the provided database
 * will live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of [MyDatabase].
     *
     * Builds the Room database named "alarm_database" using the application context.
     *
     * @param context The application context, injected by Hilt.
     * @return A singleton instance of [MyDatabase].
     */
    @Provides
    @Singleton
    fun provideMyDatabase(@ApplicationContext context: Context): MyDatabase {
        val dpsContext = context.createDeviceProtectedStorageContext()
        return Room.databaseBuilder(
            dpsContext.applicationContext,
            MyDatabase::class.java,
            "alarm_database"
        ).build()
    }

}