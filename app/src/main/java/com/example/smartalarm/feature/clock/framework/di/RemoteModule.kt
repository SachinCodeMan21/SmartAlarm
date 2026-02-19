package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.BuildConfig
import com.example.smartalarm.feature.clock.data.remote.api.GoogleApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dagger-Hilt module that provides all necessary dependencies for remote API communication,
 * specifically for interacting with the Google Places and Time Zone APIs.
 *
 * Responsibilities:
 * - Provide configured [retrofit2.Retrofit] instance.
 * - Provide [okhttp3.OkHttpClient] with interceptors for logging and API key injection.
 * - Provide [com.google.gson.Gson] instance for JSON serialization/deserialization.
 * - Provide [com.example.smartalarm.feature.clock.data.remote.api.GoogleApiService] implementation for API interaction.
 */
@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    private const val GOOGLE_BASE_API = "https://maps.googleapis.com/maps/api/"

    /**
     * Provides a Gson instance used by Retrofit for parsing JSON responses.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    /**
     * Provides a logging interceptor that logs HTTP request and response data.
     *
     * Level.BODY logs request/response lines and their respective headers and bodies (if present).
     * Useful for debugging network communication.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * Provides an interceptor that automatically appends the Google API key
     * to every outgoing request as a query parameter.
     */
    @Provides
    @Singleton
    fun provideGoogleApiKeyInterceptor(): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.GOOGLE_API_KEY)
                .build()

            val newRequest = original.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }

    /**
     * Provides a configured OkHttpClient with both the API key and logging interceptors attached.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    /**
     * Provides a Retrofit instance configured with:
     * - Google Maps base API URL
     * - Gson converter
     * - Custom OkHttpClient
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(GOOGLE_BASE_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    /**
     * Provides the implementation of [com.example.smartalarm.feature.clock.data.remote.api.GoogleApiService] using the configured Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideGoogleApiService(
        retrofit: Retrofit
    ): GoogleApiService =
        retrofit.create(GoogleApiService::class.java)
}