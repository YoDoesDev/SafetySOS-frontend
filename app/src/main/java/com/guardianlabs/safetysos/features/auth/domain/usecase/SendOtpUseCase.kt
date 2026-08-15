package com.guardianlabs.safetysos.features.auth.domain.usecase

import android.app.Activity
import com.guardianlabs.safetysos.features.auth.domain.repositories.PhoneAuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val phoneAuthRepository: PhoneAuthRepository
) {
    operator fun invoke(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        phoneAuthRepository.sendOtp(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = onCodeSent,
            onError = onError
        )
    }
}

