package com.resq.features.contacts.domain.usecase

import com.resq.core.data.local.database.entities.ContactsEntity
import com.resq.features.contacts.data.repositories.SessionExpiredException
import com.resq.features.contacts.domain.repositories.AddContactRepository
import com.resq.features.contacts.data.repositories.ServerErrorException
import com.resq.features.contacts.data.repositories.NoRelationException
import com.resq.features.contacts.data.repositories.SelfAddException
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
        // --- Validation Checks at the Start ---
        if (savedName.isBlank()) {
            return AddContactResult.GenericError("Please enter a saved name.")
        }

        if (fullPhoneNumber.isBlank()) {
            return AddContactResult.GenericError("Please enter a valid phone number.")
        }

        // Ensure number contains only digits with an optional leading '+'
        if (!fullPhoneNumber.matches(Regex("^\\+?[0-9]+$"))) {
            return AddContactResult.GenericError("Phone number can only contain numbers and a leading '+'.")
        }

        // Check for leading zeros (extracts digits after any '+' and checks if it starts with '0')
        val numberPart = if (fullPhoneNumber.startsWith("+")) fullPhoneNumber.substring(1) else fullPhoneNumber
        if (numberPart.startsWith("0")) {
            return AddContactResult.GenericError("Phone number cannot start with zero.")
        }

        // 1. Check for local contact duplicate
        if (addContactRepository.existsByPhone(fullPhoneNumber)) {
            return AddContactResult.DuplicateError("A contact with this phone number already exists.")
        }
        
        // 2. Check if user is trying to add themselves
        if (addContactRepository.isSelf(fullPhoneNumber)) {
            return AddContactResult.GenericError("You can't save yourself as an emergency contact.")
        }

        var isResQUser = false
        val trimmedUsername = username?.trim()

        // 3. Perform optional online username verification
        if (!trimmedUsername.isNullOrEmpty()) {
            try {
                isResQUser = verifyUsernameUseCase(trimmedUsername, fullPhoneNumber) 
                if (!isResQUser) {
                    return AddContactResult.GenericError("Username doesn't exist.")
                }
            } catch (e: SessionExpiredException) {
                return AddContactResult.GenericError(e.message ?: "Session expired. Reopen the app.")
            } catch (e: ServerErrorException) {
                return AddContactResult.GenericError(e.message ?: "Internal Server Error (500)")
            } catch (e: NoRelationException) {
                return AddContactResult.GenericError(e.message ?: "The phone number and the username do not seem to belong to one user.")
            } catch (e: SelfAddException) {
                return AddContactResult.GenericError(e.message ?: "Too lonely? We can call the authorities during an emergency, though...")
            } catch (e: Exception) {
                return AddContactResult.GenericError("Failed to verify username.")
            }
        }

        // 4. Save contact into Room with accurate existsOnResQ flag
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
