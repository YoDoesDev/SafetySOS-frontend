package com.resq.features.auth.domain.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.LoginResult

interface LoginInterface {
    suspend fun login(phone: String, password: String): NetworkResult<LoginResult>
}