package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.AuthTokens
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordOtpInterface
import javax.inject.Inject

class ForgotPasswordOtpCase @Inject constructor(
    private val repository: ForgotPasswordOtpInterface
) {
    suspend operator fun invoke(
        phone: String,
        uid: String
    ): NetworkResult<AuthTokens> {
        if (phone.isBlank()) {
            return NetworkResult.Error("Phone number is missing.")
        }
        if (uid.isBlank()) {
            return NetworkResult.Error("Firebase verification failed. Please resend OTP.")
        }

        return repository.verifyForgotPasswordOtp(
            phone = phone,
            uid = uid
        )
    }
}
