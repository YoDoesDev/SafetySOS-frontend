package com.resq.features.settings.domain.usecase

import com.resq.features.settings.domain.repositories.LogoutRepository
import javax.inject.Inject

class LogoutCase @Inject constructor(
    private val repository: LogoutRepository
)   {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}