package com.resq.features.presets.domain.repositories

import com.resq.core.data.local.database.entities.ContactsEntity
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getAllContacts(savedBy: String): Flow<List<ContactsEntity>>
}
