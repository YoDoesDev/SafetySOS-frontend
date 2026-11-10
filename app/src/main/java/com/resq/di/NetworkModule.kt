package com.resq.di

import android.content.Context
import com.resq.core.data.remote.interceptors.RetryInterceptor
import com.resq.core.data.remote.interceptors.AuthInterceptor 
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.splash.data.remote.SplashApiService
import com.resq.features.contacts.data.remote.ContactsApiService
import com.resq.features.settings.data.remote.SettingsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext 
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
         // retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) 
            // .addInterceptor(retryInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient 
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://safetysos-api.onrender.com/")
            .client(okHttpClient)
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
    
    @Provides
    @Singleton
    fun provideContactsApiService(
        retrofit: Retrofit
    ): ContactsApiService = retrofit.create(ContactsApiService::class.java)
    
    @Provides
    @Singleton
    fun provideSettingsApiService(
        retrofit: Retrofit
    ): SettingsApiService = retrofit.create(SettingsApiService::class.java)
}
