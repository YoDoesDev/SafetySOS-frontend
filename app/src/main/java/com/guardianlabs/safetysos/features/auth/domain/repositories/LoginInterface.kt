package com.guardianlabs.safetysos.features.auth.domain.repositories

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.LoginResult

interface LoginInterface {
    suspend fun login(phone: String, password: String): NetworkResult<LoginResult>
}
