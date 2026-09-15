package com.guardianlabs.safetysos.features.auth.data.repositories

import com.guardianlabs.safetysos.core.data.local.preferences.TokenManager
import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.data.remote.dto.ForgotPasswordOtpRequestDto
import com.guardianlabs.safetysos.features.auth.data.remote.dto.ForgotPasswordOtpResponseDto
import com.guardianlabs.safetysos.features.auth.domain.models.AuthTokens
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordOtpInterface
import javax.inject.Inject
import java.io.IOException
class ForgotPasswordOtpInterfaceImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : ForgotPasswordOtpInterface {

    override suspend fun verifyForgotPasswordOtp(
        phone: String,
        uid: String
    ): NetworkResult<AuthTokens> {
        return try {
            val httpResponse = apiService.forgotPasswordLogin(
                ForgotPasswordOtpRequestDto(
                    phone = phone,
                    uid = uid
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
                        tokenManager.resetSessionExpiry()
                    }
                   tokenManager.resetSessionExpiry()
                    NetworkResult.Success(AuthTokens(accessToken = accessToken, refreshToken = refreshToken))
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
