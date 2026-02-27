package com.example.smartalarm.di.global

import android.content.Context
import androidx.room.Room
import com.example.smartalarm.core.data.database.MyDatabase
import com.example.smartalarm.core.framework.di.modules.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

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
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            MyDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

}