package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import javax.inject.Inject
import com.guardianlabs.safetysos.features.auth.domain.models.HiltCheck


class RegisterCase @Inject constructor(
    private val repository: RegisterInterface
) {
    suspend operator fun invoke(
  message: String
): Result<User> {
    return repository.register(
        phone, password, username
    )
}
}
