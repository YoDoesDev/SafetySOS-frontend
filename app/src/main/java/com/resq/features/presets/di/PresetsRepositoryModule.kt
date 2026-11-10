package com.resq.features.presets.di

import com.resq.features.presets.data.repositories.*
import com.resq.features.presets.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresetsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindContactsRepository(
        impl: ContactsRepositoryImpl
    ): ContactsRepository
    
    @Binds
    @Singleton
    abstract fun bindPresetsRepository(
        impl: PresetsRepositoryImpl
    ): PresetsRepository

}
