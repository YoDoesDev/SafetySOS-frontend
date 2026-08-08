package com.guardianlabs.safetysos.di

import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.repositories.RegisterInterfaceImpl
import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRegisterRepository(
        api: AuthApiService
    ): RegisterInterface {
        return RegisterInterfaceImpl(api)
    }
}