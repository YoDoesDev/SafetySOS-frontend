package com.resq.features.auth.data.repositories

import com.resq.core.data.local.preferences.TokenManager
import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.auth.data.remote.dto.LoginRequestDto
import com.resq.features.auth.domain.models.LoginResult
import com.resq.features.auth.domain.repositories.LoginInterface
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class LoginInterfaceImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager,
    private val dao: UserDao
) : LoginInterface {

    override suspend fun login(phone: String, password: String): NetworkResult<LoginResult> {
        return try {
            val request = LoginRequestDto(phone = phone, password = password)
            val httpResponse = api.login(request)

            if (httpResponse.isSuccessful && httpResponse.body() != null) {
                val body = httpResponse.body()!!
                val accessToken = body.accessToken
                val refreshToken = body.refreshToken

                if (!accessToken.isNullOrBlank()) {
                    // Save tokens securely in EncryptedPrefs via TokenManager
                    tokenManager.saveAccessToken(accessToken)
                    
                    if (!refreshToken.isNullOrBlank()) {
                        tokenManager.saveRefreshToken(refreshToken)
                        tokenManager.resetSessionExpiry()
                    }
                    tokenManager.resetSessionExpiry()
                    val userEntity = UserEntity(
                        username = body.username ?: "", 
                        phone = body.phone ?: ""
                    )
                    dao.setUser(user = userEntity)
                    NetworkResult.Success(LoginResult(accessToken = accessToken, refreshToken = refreshToken))
                } else {
                    NetworkResult.Error("Authentication token missing")
                }
            } else {
                val rawError = httpResponse.errorBody()?.string().orEmpty().trim()
                val cleanMessage = try {
                    val json = JSONObject(rawError)
                    json.optString("reason", json.optString("message", "Login failed"))
                } catch (e: Exception) {
                    rawError.takeIf { it.isNotBlank() } ?: "Login failed (${httpResponse.code()})"
                }

                NetworkResult.Error(message = cleanMessage, code = httpResponse.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("No internet connection. Please try again.")
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred.")
        }
    }
}
