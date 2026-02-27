package com.example.smartalarm.core.framework.di.modules

import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.core.framework.sharedPreference.impl.SharedPrefsHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SharedPreferenceModule {

    @Binds
    @Singleton
    abstract fun bindSharedPrefHelper(impl: SharedPrefsHelperImpl) : SharedPrefsHelper

}