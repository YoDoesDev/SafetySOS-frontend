package com.resq.features.auth.domain.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.RegisterCheck

interface RegisterInterface {
    suspend fun register(phone: String, password: String, username: String): NetworkResult<RegisterCheck>
}