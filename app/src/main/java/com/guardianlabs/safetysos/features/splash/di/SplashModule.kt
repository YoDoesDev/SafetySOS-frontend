package com.guardianlabs.safetysos.features.splash.di

import com.guardianlabs.safetysos.features.splash.data.repositories.SplashRepositoryImpl
import com.guardianlabs.safetysos.features.splash.domain.repositories.SplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashModule {

    @Binds
    @Singleton
    abstract fun bindSplashRepository(
        splashRepositoryImpl: SplashRepositoryImpl
    ): SplashRepository
}
