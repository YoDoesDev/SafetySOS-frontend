package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.ForgotPasswordCheck

interface ForgotPasswordInterface {
    suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordCheck>
}
