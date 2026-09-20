package com.resq.features.contacts.domain.repositories

import com.resq.core.data.local.database.entities.ContactsEntity
import kotlinx.coroutines.flow.Flow

interface AddContactRepository {
    fun getAllContacts(): Flow<List<ContactsEntity>>
    suspend fun existsByName(name: String): Boolean
    suspend fun existsByPhone(phone: String): Boolean
    suspend fun addContact(contact: ContactsEntity)
    suspend fun deleteContact(contact: ContactsEntity)
}
