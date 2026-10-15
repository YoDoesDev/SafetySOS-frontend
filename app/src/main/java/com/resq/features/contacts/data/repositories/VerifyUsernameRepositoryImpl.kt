package com.resq.features.contacts.data.repositories

import com.resq.features.contacts.data.remote.ContactsApiService
import com.resq.features.contacts.data.remote.dto.VerifyUsernameRequest
import com.resq.features.contacts.domain.repositories.VerifyUsernameRepository
import com.resq.core.data.local.preferences.TokenManager
import com.resq.core.data.local.database.dao.UserDao
import javax.inject.Inject

class SessionExpiredException(
    message: String = "Session expired. Reopen the app."
) : Exception(message)

class ServerErrorException(
    message: String = "Internal Server Error (500)."
) : Exception(message)

class NoRelationException(
    message: String = "The phone number and the username do not seem to belong to one user. Try a different username or phone number or leave the username blank if they do not have an account on ResQ."
): Exception(message)

class SelfAddException(
    message: String = "Too lonely? We can call the authorities during an emergency, though..."
): Exception(message)

class VerifyUsernameRepositoryImpl @Inject constructor(
    private val apiService: ContactsApiService, 
    private val tokenManager: TokenManager,
    private val user: UserDao
) : VerifyUsernameRepository {

    override suspend fun verifyUsernameExists(username: String, phone: String): Boolean {
        return try {
            val token = tokenManager.getAccessToken()
            val authHeader = if (token?.startsWith("Bearer ") == true) token else "Bearer $token"
            val response = apiService.verifyUsername(
                authHeader, 
                VerifyUsernameRequest(username = username, phone = phone)
            )

            when {
                response.isSuccessful -> {
                    if (response.body()?.reason?.contains("does not belong") == true) {
                        throw NoRelationException()
                    }
                    response.body()?.exists ?: false
                }

                response.code() == 401 || response.code() == 403 -> {
                    throw SessionExpiredException()
                }
                response.code() == 500 -> {
                    throw ServerErrorException()
                }
                else -> {
                    // Fail-open for other HTTP server errors
                    throw ServerErrorException()
                }
            }
        } catch (e: SessionExpiredException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: ServerErrorException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: NoRelationException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: SelfAddException) {
            // Re-throw so UseCase catches session failures explicitly
            throw e
        } catch (e: Exception) {
            // Offline / network exception fallback (fail-open)
          throw e
        }
    }
}
