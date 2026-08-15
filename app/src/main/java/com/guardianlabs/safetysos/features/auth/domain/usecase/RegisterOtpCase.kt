package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import javax.inject.Inject
import com.guardianlabs.safetysos.features.auth.domain.models.*


class RegisterOtpCase @Inject constructor(
    private val repository: RegisterOtpInterface
) {
    suspend operator fun invoke(
  username: String, 
  phone: String,
  password: String,
  uid: String
): Result<User> {
    return repository.register(
        username, phone, password, uid
    )
}
}
