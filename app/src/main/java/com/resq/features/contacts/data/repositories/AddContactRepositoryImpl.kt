package com.resq.features.contacts.data.repositories

import com.resq.core.data.local.database.dao.ContactsDao
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.contacts.domain.repositories.AddContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddContactRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao
) : AddContactRepository {

    override fun getAllContacts(): Flow<List<ContactsEntity>> = contactsDao.getAllContacts()

    override suspend fun existsByName(name: String): Boolean = contactsDao.existsByName(name)

    override suspend fun existsByPhone(phone: String): Boolean = contactsDao.existsByPhone(phone)

    override suspend fun addContact(contact: ContactsEntity) {
        contactsDao.upsertContact(contact)
    }

    override suspend fun deleteContact(contact: ContactsEntity) {
        contactsDao.deleteContact(contact)
    }
}
