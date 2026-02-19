package com.example.smartalarm.core.di.modules

import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.number.NumberFormatterImpl
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.core.utility.formatter.time.TimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FormatterModule {

    /**
     * Binds [NumberFormatterImpl] as the implementation of [NumberFormatter].
     *
     * This method tells Dagger to provide an instance of [NumberFormatterImpl]
     * whenever [NumberFormatter] is requested.
     */
    @Binds
    @Singleton
    abstract fun bindNumberFormatter(numberFormatterImpl: NumberFormatterImpl): NumberFormatter

    /**
     * Binds [TimeFormatterImpl] as the implementation of [TimeFormatter].
     *
     * This method tells Dagger to provide an instance of [TimeFormatterImpl]
     * whenever [TimeFormatter] is requested.
     */
    @Binds
    @Singleton
    abstract fun bindTimeFormatter(timeFormatterImpl: TimeFormatterImpl): TimeFormatter
}