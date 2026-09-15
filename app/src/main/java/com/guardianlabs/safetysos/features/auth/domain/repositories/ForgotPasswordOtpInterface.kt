package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.AuthTokens

interface ForgotPasswordOtpInterface {

    suspend fun verifyForgotPasswordOtp(
        phone: String,
        uid: String
    ): NetworkResult<AuthTokens>
}
