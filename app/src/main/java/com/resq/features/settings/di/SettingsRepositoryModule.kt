package com.resq.features.settings.di

import com.resq.features.settings.domain.repositories.LogoutRepository
import com.resq.features.settings.data.repositories.LogoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLogoutRepository(
        impl: LogoutRepositoryImpl
    ): LogoutRepository
}
