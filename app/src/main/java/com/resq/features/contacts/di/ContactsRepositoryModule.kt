package com.resq.features.contacts.di

import com.resq.features.contacts.data.repositories.AddContactRepositoryImpl
import com.resq.features.contacts.data.repositories.VerifyUsernameRepositoryImpl
import com.resq.features.contacts.domain.repositories.AddContactRepository
import com.resq.features.contacts.domain.repositories.VerifyUsernameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVerifyUsernameRepository(
        impl: VerifyUsernameRepositoryImpl
    ): VerifyUsernameRepository

    @Binds
    @Singleton
    abstract fun bindAddContactRepository(
        impl: AddContactRepositoryImpl
    ): AddContactRepository
}
