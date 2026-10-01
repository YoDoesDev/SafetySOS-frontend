package com.resq.features.contacts.domain.usecase

import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.contacts.data.repositories.SessionExpiredException
import com.resq.features.contacts.domain.repositories.AddContactRepository
import javax.inject.Inject

sealed class AddContactResult {
    object Success : AddContactResult()
    data class DuplicateError(val message: String) : AddContactResult()
    data class GenericError(val message: String) : AddContactResult()
}

class AddContactUseCase @Inject constructor(
    private val addContactRepository: AddContactRepository,
    private val verifyUsernameUseCase: VerifyUsernameUseCase
) {
    suspend operator fun invoke(
        savedName: String, 
        fullPhoneNumber: String, 
        username: String?
    ): AddContactResult {
        // 1. Check for local contact duplicate
        if (addContactRepository.existsByPhone(fullPhoneNumber)) {
            return AddContactResult.DuplicateError("A contact with this phone number already exists.")
        }

        var isResQUser = false
        val trimmedUsername = username?.trim()

        // 2. Perform optional online username verification
        if (!trimmedUsername.isNullOrEmpty()) {
            try {
                // Returns Boolean (true/false) directly based on repository logic
                isResQUser = verifyUsernameUseCase(trimmedUsername)
            } catch (e: SessionExpiredException) {
                // Session expired — stop contact creation and surface error
                return AddContactResult.GenericError(e.message ?: "Session expired. Reopen the app.")
            } catch (e: Exception) {
                // Any unhandled exception fallback
                return AddContactResult.GenericError("Failed to verify username.")
            }
        }

        // 3. Save contact into Room with accurate existsOnResQ flag
        val contact = ContactsEntity(
            savedName = savedName,
            phoneNumber = fullPhoneNumber,
            existsOnResQ = isResQUser,
            username = if (isResQUser) trimmedUsername else null
        )

        addContactRepository.addContact(contact)
        return AddContactResult.Success
    }
}
