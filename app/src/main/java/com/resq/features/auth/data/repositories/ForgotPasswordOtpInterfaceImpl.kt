package com.resq.features.auth.data.repositories

import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity
import com.resq.core.data.local.preferences.TokenManager
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.data.remote.dto.ForgotPasswordOtpRequestDto
import com.resq.features.auth.domain.models.AuthTokens
import com.resq.features.auth.domain.repositories.ForgotPasswordOtpInterface
import javax.inject.Inject
import java.io.IOException

class ForgotPasswordOtpInterfaceImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val dao: UserDao
) : ForgotPasswordOtpInterface {

    override suspend fun verifyForgotPasswordOtp(
        phone: String,
        uid: String
    ): NetworkResult<AuthTokens> {
        return try {
            val httpResponse = apiService.forgotPasswordLogin(
                ForgotPasswordOtpRequestDto(
                    phone = phone,
                    idToken = uid
                )
            )

            if (httpResponse.isSuccessful && httpResponse.body() != null) {
                val responseDto = httpResponse.body()!!
                val accessToken = responseDto.accessToken
                val refreshToken = responseDto.refreshToken

                if (!accessToken.isNullOrBlank()) {
                    // Save tokens securely in EncryptedPrefs via TokenManager
                    tokenManager.saveAccessToken(accessToken)

                    if (!refreshToken.isNullOrBlank()) {
                        tokenManager.saveRefreshToken(refreshToken)
                    }
                    tokenManager.resetSessionExpiry()

                    // Save the user in Room so Home doesn't show "unknown user"
                    dao.setUser(
                        user = UserEntity(
                            username = responseDto.username ?: "",
                            phone = responseDto.phone ?: phone
                        )
                    )

                    NetworkResult.Success(
                        AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
                    )
                } else {
                    NetworkResult.Error("Authentication token missing")
                }
            } else {
                NetworkResult.Error(
                    httpResponse.errorBody()?.string()?.ifBlank { "Verification failed." }
                        ?: "Failed to verify OTP with backend."
                )
            }
        } catch (e: IOException) {
            NetworkResult.Error("No internet connection. Please try again.")
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network error occurred.")
        }
    }
}
