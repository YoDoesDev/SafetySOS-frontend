package com.guardianlabs.safetysos.features.auth.domain.repositories

import android.app.Activity

interface PhoneAuthRepository {

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (message: String) -> Unit
    )

    suspend fun verifyOtpCode(
        verificationId: String,
        userCode: String
    ): Result<String>
}
