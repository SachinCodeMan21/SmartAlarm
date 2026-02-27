package com.example.testutils.sharedModules

import com.example.smartalarm.core.framework.di.modules.SharedPreferenceModule
import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.testutils.sharedFake.FakeSharedPrefsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SharedPreferenceModule::class]
)
abstract class TestSharedPreferenceModule {

    @Binds
    @Singleton
    abstract fun bindSharedPrefHelper(impl: FakeSharedPrefsHelper): SharedPrefsHelper
}
