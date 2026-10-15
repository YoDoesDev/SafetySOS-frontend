package com.resq.features.contacts.domain.usecase

import com.resq.features.contacts.domain.repositories.VerifyUsernameRepository
import com.resq.features.contacts.data.repositories.SessionExpiredException
import com.resq.features.contacts.data.repositories.ServerErrorException
import com.resq.features.contacts.data.repositories.NoRelationException
import javax.inject.Inject

class VerifyUsernameUseCase @Inject constructor(
    private val repository: VerifyUsernameRepository
)   {
    suspend operator fun invoke(username: String, phone: String): Boolean{
        return try {
            val exists = repository.verifyUsernameExists(username, phone)
            exists
        } catch (e: SessionExpiredException) {
            throw e
            } catch (e: ServerErrorException) {
            throw e
            } catch (e: NoRelationException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: Exception) {
            // Offline / network exception fallback (fail-open)
          throw e
        }
    }
}
