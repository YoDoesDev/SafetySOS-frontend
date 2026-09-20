package com.resq.features.contacts.data.repositories

import com.resq.features.contacts.data.remote.ContactsApiService
import com.resq.features.contacts.data.remote.dto.VerifyUsernameRequest
import com.resq.features.contacts.domain.repositories.VerifyUsernameRepository
import com.resq.core.data.local.preferences.TokenManager
import javax.inject.Inject

class SessionExpiredException(
    message: String = "Session expired. Reopen the app."
) : Exception(message)

class VerifyUsernameRepositoryImpl @Inject constructor(
    private val apiService: ContactsApiService, 
    private val tokenManager: TokenManager
) : VerifyUsernameRepository {

    override suspend fun verifyUsernameExists(username: String): Boolean {
        return try {
            val token = tokenManager.getAccessToken()
            val authHeader = if (token?.startsWith("Bearer ") == true) token else "Bearer $token"

            val response = apiService.verifyUsername(
                authHeader, 
                VerifyUsernameRequest(username = username)
            )

            when {
                response.isSuccessful -> {
                    response.body()?.exists == true
                }
                response.code() == 401 || response.code() == 403 -> {
                    throw SessionExpiredException()
                }
                else -> {
                    // Fail-open for other HTTP server errors
                    false
                }
            }
        } catch (e: SessionExpiredException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: Exception) {
            // Offline / network exception fallback (fail-open)
            false
        }
    }
}
