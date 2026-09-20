package com.resq.features.splash.di

import com.resq.features.splash.data.repositories.SplashRepositoryImpl
import com.resq.features.splash.domain.repositories.SplashRepository
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
