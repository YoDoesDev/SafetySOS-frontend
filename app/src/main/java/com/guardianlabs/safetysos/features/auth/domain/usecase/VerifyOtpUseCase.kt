package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.domain.repositories.PhoneAuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository
) {
    suspend operator fun invoke(
        verificationId: String,
        userCode: String
    ): Result<String> {
        return phoneAuthRepository.verifyOtpCode(
            verificationId = verificationId,
            userCode = userCode
        )
    }
}
