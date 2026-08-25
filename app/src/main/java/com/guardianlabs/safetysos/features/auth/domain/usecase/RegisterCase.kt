package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import javax.inject.Inject
import com.guardianlabs.safetysos.features.auth.domain.models.*
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult

class RegisterCase @Inject constructor(
    private val repository: RegisterInterface
) {
    suspend operator fun invoke(
  phone: String,
  password: String, 
  username: String
): NetworkResult<RegisterCheck> {
    return repository.register(
        phone, password, username
    )
}
}
