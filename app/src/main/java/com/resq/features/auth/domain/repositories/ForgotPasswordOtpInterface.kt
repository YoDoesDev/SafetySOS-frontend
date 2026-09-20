package com.resq.features.auth.domain.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.AuthTokens

interface ForgotPasswordOtpInterface {

    suspend fun verifyForgotPasswordOtp(
        phone: String,
        uid: String
    ): NetworkResult<AuthTokens>
}
