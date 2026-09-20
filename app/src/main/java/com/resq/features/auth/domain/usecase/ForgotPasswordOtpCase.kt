package com.resq.features.auth.domain.usecase

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.AuthTokens
import com.resq.features.auth.domain.repositories.ForgotPasswordOtpInterface
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
