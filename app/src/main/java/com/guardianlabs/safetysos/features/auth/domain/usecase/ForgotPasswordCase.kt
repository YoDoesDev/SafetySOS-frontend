package com.guardianlabs.safetysos.features.auth.domain.usecase

import com.guardianlabs.safetysos.features.auth.data.network.ForgotPasswordResponseDto
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordInterface
import javax.inject.Inject

class ForgotPasswordCaseImpl @Inject constructor(
    private val forgotPasswordInterface: ForgotPasswordInterface
) : ForgotPasswordCase {

    override suspend operator fun invoke(phone: String): NetworkResult<ForgotPasswordResponseDto> {
        return forgotPasswordInterface.checkPhone(phone)
    }
}
