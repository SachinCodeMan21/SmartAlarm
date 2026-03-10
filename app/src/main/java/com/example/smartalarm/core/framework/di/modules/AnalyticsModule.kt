package com.example.smartalarm.core.framework.di.modules

import android.content.Context
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.analytics.ErrorLogger
import com.example.smartalarm.core.framework.analytics.FirebaseAnalyticsHelper
import com.example.smartalarm.core.framework.analytics.FirebaseErrorLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    // 1. We use a companion object for @Provides because FirebaseAnalytics
    // is an external dependency we don't own the constructor for.
    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
            return FirebaseCrashlytics.getInstance()
        }
    }

    // 2. We use @Binds for our internal implementations to link the
    // Interface to the Class Implementation.
    @Binds
    @Singleton
    abstract fun bindAnalyticsHelper(
        impl: FirebaseAnalyticsHelper
    ): AnalyticsHelper

    @Binds
    @Singleton
    abstract fun bindErrorLogger(
        impl: FirebaseErrorLogger
    ): ErrorLogger
}