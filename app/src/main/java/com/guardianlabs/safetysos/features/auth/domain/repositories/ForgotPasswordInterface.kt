package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.data.network.ForgotPasswordResponseDto
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult

interface ForgotPasswordInterface {
    suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordResponseDto>
}
