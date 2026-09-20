package com.resq.features.auth.domain.usecase

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.User
import com.resq.features.auth.domain.repositories.RegisterOtpInterface
import javax.inject.Inject

class RegisterOtpCase @Inject constructor(
    private val repository: RegisterOtpInterface
) {
    suspend operator fun invoke(
        username: String, 
        phone: String,
        password: String,
        uid: String
    ): NetworkResult<User> {
        return repository.register(
            username = username, 
            phone = phone, 
            password = password, 
            uid = uid
        )
    }
}
