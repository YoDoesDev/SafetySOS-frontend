package com.resq.di

import com.resq.core.data.remote.interceptors.RetryInterceptor
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.splash.data.remote.SplashApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /*
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(maxRetries = 3))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    */
    @Provides
    @Singleton
    fun provideRetrofit(
        // okHttpClient: OkHttpClient // Attaches the OkHttpClient to Retrofit
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://safetysos-api.onrender.com/")
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideSplashApi(
        retrofit: Retrofit 
    ): SplashApiService = retrofit.create(SplashApiService::class.java)
}