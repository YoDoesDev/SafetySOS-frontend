package com.resq.features.presets.domain.usecase

import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.presets.domain.repositories.ContactsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository
) {
    operator fun invoke(savedBy: String): Flow<List<ContactsEntity>> {
        return contactsRepository.getAllContacts(savedBy)
    }
}
