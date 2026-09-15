package com.resq.features.auth.di

import com.resq.features.auth.data.repositories.PhoneAuthRepositoryImpl
import com.resq.features.auth.data.repositories.RegisterInterfaceImpl
import com.resq.features.auth.domain.repositories.PhoneAuthRepository
import com.resq.features.auth.domain.repositories.RegisterInterface
import com.resq.features.auth.data.repositories.RegisterOtpInterfaceImpl
import com.resq.features.auth.domain.repositories.RegisterOtpInterface
import com.resq.features.auth.data.repositories.LoginInterfaceImpl
import com.resq.features.auth.domain.repositories.LoginInterface
import com.resq.features.auth.data.repositories.ForgotPasswordInterfaceImpl
import com.resq.features.auth.domain.repositories.ForgotPasswordInterface
import com.resq.features.auth.data.repositories.ForgotPasswordOtpInterfaceImpl
import com.resq.features.auth.domain.repositories.ForgotPasswordOtpInterface

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
    
    @Binds
    @Singleton
    abstract fun bindRegisterOtpRepository(
        impl: RegisterOtpInterfaceImpl
    ): RegisterOtpInterface
    
    @Binds
    @Singleton
    abstract fun bindLoginInterface(
        loginInterfaceImpl: LoginInterfaceImpl
    ): LoginInterface

    @Binds
    @Singleton
    abstract fun bindForgotPasswordInterface(
        impl: ForgotPasswordInterfaceImpl
    ): ForgotPasswordInterface
    
    @Binds
    @Singleton
    abstract fun bindForgotPasswordOtpInterface(
        impl: ForgotPasswordOtpInterfaceImpl
    ): ForgotPasswordOtpInterface

}