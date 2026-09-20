package com.resq.features.auth.domain.usecase

import android.app.Activity
import com.resq.features.auth.domain.repositories.PhoneAuthRepository
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
        // Strip out non-digits first
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        
        // E.164 Formatting Guard
        val formattedNumber = when {
            digitsOnly.length == 10 -> "+91$digitsOnly"
            digitsOnly.length == 12 && digitsOnly.startsWith("91") -> "+$digitsOnly"
            phoneNumber.startsWith("+") -> phoneNumber.replace(Regex("[^0-9+]"), "")
            else -> "+91$digitsOnly"
        }

        if (digitsOnly.length < 10) {
            onError("Please enter a valid 10-digit mobile number.")
            return
        }

        phoneAuthRepository.sendOtp(
            phoneNumber = formattedNumber,
            activity = activity,
            onCodeSent = onCodeSent,
            onError = onError
        )
  
    }
}
