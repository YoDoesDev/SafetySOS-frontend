package com.resq.features.auth.data.repositories

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.LoginResult
import com.resq.features.auth.domain.repositories.LoginInterface
import javax.inject.Inject

class LoginInterfaceImpl @Inject constructor() : LoginInterface {
    override suspend fun login(phone: String, password: String): NetworkResult<LoginResult> {
        return try {
            NetworkResult.Success(LoginResult("", "", com.resq.features.auth.domain.models.User("", "", "", false)))
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}