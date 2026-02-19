package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.framework.di.annotations.TimerServiceScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ServiceComponent::class)
object ServiceScopeModule {

    @TimerServiceScope
    @Provides
    fun provideServiceCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
}