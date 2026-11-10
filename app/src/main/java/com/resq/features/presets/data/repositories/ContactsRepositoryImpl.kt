package com.resq.features.presets.data.repositories

import com.resq.core.data.local.database.dao.ContactsDao
import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.presets.domain.repositories.ContactsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao
) : ContactsRepository {

    override fun getAllContacts(savedBy: String): Flow<List<ContactsEntity>> {
        return contactsDao.getAllContacts(savedBy)
    }
}
