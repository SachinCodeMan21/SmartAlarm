package com.example.smartalarm.core.di.modules

import androidx.room.Insert
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.core.utility.sharedPreference.impl.SharedPrefsHelperImpl
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