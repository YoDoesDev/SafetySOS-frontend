package com.guardianlabs.safetysos.features.auth.di

import com.guardianlabs.safetysos.features.auth.data.repositories.PhoneAuthRepositoryImpl
import com.guardianlabs.safetysos.features.auth.data.repositories.RegisterInterfaceImpl
import com.guardianlabs.safetysos.features.auth.domain.repositories.PhoneAuthRepository
import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhoneAuthRepository(
        impl: PhoneAuthRepositoryImpl
    ): PhoneAuthRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        impl: RegisterInterfaceImpl
    ): RegisterInterface
}
