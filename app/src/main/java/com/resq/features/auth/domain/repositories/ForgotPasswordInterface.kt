package com.resq.features.auth.domain.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.ForgotPasswordCheck

interface ForgotPasswordInterface {
    suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordCheck>
}
