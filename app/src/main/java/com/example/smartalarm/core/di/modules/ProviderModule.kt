package com.example.smartalarm.core.di.modules

import com.example.smartalarm.core.utility.provider.locale.LocaleProvider
import com.example.smartalarm.core.utility.provider.locale.LocaleProviderImpl
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.provider.resource.impl.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {

    @Binds
    @Singleton
    abstract fun bindLocaleProvider(localeProviderImpl: LocaleProviderImpl): LocaleProvider

    @Binds
    @Singleton
    abstract fun bindResourceProvider(impl: ResourceProviderImpl): ResourceProvider

}