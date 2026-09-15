package com.resq.features.auth.data.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.RegisterCheck
import com.resq.features.auth.domain.repositories.RegisterInterface
import javax.inject.Inject

class RegisterInterfaceImpl @Inject constructor() : RegisterInterface {
    override suspend fun register(phone: String, password: String, username: String): NetworkResult<RegisterCheck> {
        return try {
            NetworkResult.Success(RegisterCheck("Registration successful", true))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}