package com.example.smartalarm.core.framework.di.modules

import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.number.NumberFormatterImpl
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

}