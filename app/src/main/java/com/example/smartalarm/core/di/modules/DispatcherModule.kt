package com.example.smartalarm.core.di.modules

import com.example.smartalarm.core.di.annotations.ApplicationScope
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.di.annotations.IoDispatcher
import com.example.smartalarm.core.di.annotations.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module that provides different [CoroutineDispatcher] instances
 * for dependency injection.
 *
 * This helps inject appropriate dispatchers (IO, Default, Main) using custom
 * qualifiers, improving testability and separation of concerns.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    /**
     * Provides the IO dispatcher, typically used for network or disk operations.
     *
     * Qualified with [IoDispatcher] to distinguish it in dependency injection.
     */
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the Default dispatcher, used for CPU-intensive work.
     *
     * Qualified with [DefaultDispatcher] to distinguish it in dependency injection.
     */
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides the Main dispatcher, typically used for UI-related operations.
     *
     * Qualified with [MainDispatcher] to distinguish it in dependency injection.
     */
    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main


    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        // SupervisorJob ensures one failure doesn't kill the whole app's logic
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

}
