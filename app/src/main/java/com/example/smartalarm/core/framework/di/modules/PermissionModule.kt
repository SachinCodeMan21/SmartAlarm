package com.example.smartalarm.core.framework.di.modules


import com.example.smartalarm.core.framework.permission.PermissionManager
import com.example.smartalarm.core.framework.permission.PermissionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindPermissionManager(impl: PermissionManagerImpl) : PermissionManager

}