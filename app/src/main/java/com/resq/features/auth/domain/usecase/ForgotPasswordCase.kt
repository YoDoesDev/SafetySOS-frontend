package com.resq.features.auth.domain.usecase

import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.domain.models.ForgotPasswordCheck
import com.resq.features.auth.domain.repositories.ForgotPasswordInterface
import javax.inject.Inject

class ForgotPasswordCase @Inject constructor(
    private val repository: ForgotPasswordInterface
) {
    suspend operator fun invoke(phone: String): NetworkResult<ForgotPasswordCheck> {
        return repository.checkPhone(phone)
    }
}
