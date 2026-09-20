package com.resq.features.contacts.domain.usecase

import com.resq.features.contacts.data.repositories.SessionExpiredException
import com.resq.features.contacts.domain.repositories.VerifyUsernameRepository
import javax.inject.Inject

class VerifyUsernameUseCase @Inject constructor(
    private val repository: VerifyUsernameRepository
) {
    suspend operator fun invoke(username: String): Result<Unit> {
        return try {
            val exists = repository.verifyUsernameExists(username)
            if (exists) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Username '@$username' does not exist."))
            }
        } catch (e: SessionExpiredException) {
            Result.failure(e)
        }
    }
}
