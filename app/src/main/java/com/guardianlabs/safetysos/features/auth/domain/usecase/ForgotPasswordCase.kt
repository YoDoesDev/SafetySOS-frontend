package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.data.remote.dto.ForgotPasswordResponseDto
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordInterface
import javax.inject.Inject

class ForgotPasswordCase @Inject constructor(
    private val forgotPasswordInterface: ForgotPasswordInterface
) {
    suspend operator fun invoke(phone: String): NetworkResult<ForgotPasswordResponseDto> {
        return forgotPasswordInterface.checkPhone(phone)
    }
}
