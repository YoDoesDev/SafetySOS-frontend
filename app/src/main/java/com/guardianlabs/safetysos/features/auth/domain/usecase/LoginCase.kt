package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.models.LoginResult
import com.guardianlabs.safetysos.features.auth.domain.repositories.LoginInterface
import javax.inject.Inject

class LoginCase @Inject constructor(
    private val repository: LoginInterface
) {
    suspend operator fun invoke(phone: String, password: String): NetworkResult<LoginResult> {
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()

        val formattedPhone = if (cleanPhone.startsWith("+")) cleanPhone else "+91$cleanPhone"

        return repository.login(formattedPhone, cleanPassword)
    }
}
