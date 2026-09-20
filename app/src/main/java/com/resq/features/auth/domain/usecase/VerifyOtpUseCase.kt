package com.resq.features.auth.domain.usecase

import com.resq.features.auth.domain.repositories.PhoneAuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository
) {
    suspend operator fun invoke(
        verificationId: String,
        userCode: String
    ): Result<String> {
        // Sanity guard before calling repository
        if (verificationId.isBlank()) {
            return Result.failure(Exception("Session expired. Please tap 'Resend OTP'."))
        }
        if (userCode.length != 6) {
            return Result.failure(Exception("Please enter a valid 6-digit OTP."))
        }

        return phoneAuthRepository.verifyOtpCode(
            verificationId = verificationId,
            userCode = userCode
        )
    }
}
