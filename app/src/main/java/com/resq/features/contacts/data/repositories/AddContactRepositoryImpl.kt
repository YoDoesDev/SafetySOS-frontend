package com.resq.features.contacts.data.repositories

import com.resq.core.data.local.database.dao.*
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.contacts.domain.repositories.AddContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class AddContactRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    private val userDao: UserDao
) : AddContactRepository {

    override fun getAllContacts(): Flow<List<ContactsEntity>> = flow {
        val currentPhone = userDao.getPhone() ?: ""
        emit(currentPhone)
    }.flatMapLatest { phone ->
        contactsDao.getAllContacts(phone)
    }

    override suspend fun existsByName(name: String): Boolean {
        val currentPhone = userDao.getPhone() ?: ""
        return contactsDao.existsByName(name, currentPhone)
    }

    override suspend fun existsByPhone(phone: String): Boolean {
        val currentPhone = userDao.getPhone() ?: ""
        return contactsDao.existsByPhone(phone, currentPhone)
    }

    override suspend fun addContact(contact: ContactsEntity) {
        contactsDao.upsertContact(contact)
    }

    override suspend fun deleteContact(contact: ContactsEntity) {
        contactsDao.deleteContact(contact)
    }
    
    override suspend fun isSelf(phone: String): Boolean {
        return (userDao.getPhone() == phone)
    }
}
