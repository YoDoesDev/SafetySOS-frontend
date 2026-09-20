package com.resq.features.contacts.domain.usecase

import com.resq.core.data.local.database.entities.ContactsEntity
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

        // 2. Perform optional online username verification
        if (!username.isNullOrBlank()) {
            val verificationResult = verifyUsernameUseCase(username.trim())
            
            // Evaluates success vs failure based on the boolean result, NOT just HTTP status
            verificationResult.fold(
                onSuccess = {
                    isResQUser = true
                },
                onFailure = { exception ->
                    // Catches both { "exists": false } and SessionExpiredException
                    return AddContactResult.GenericError(
                        exception.message ?: "Failed to verify username."
                    )
                }
            )
        }

        // 3. Save contact into Room with accurate existsOnResQ flag
        val contact = ContactsEntity(
            savedName = savedName,
            phoneNumber = fullPhoneNumber,
            existsOnResQ = isResQUser,
            username = if (isResQUser) username?.trim() else null
        )

        addContactRepository.addContact(contact)
        return AddContactResult.Success
    }
}
