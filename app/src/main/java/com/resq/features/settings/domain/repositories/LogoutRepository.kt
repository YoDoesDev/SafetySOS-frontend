package com.resq.features.settings.domain.repositories

interface LogoutRepository {
    suspend fun logout(): Result<Unit>
}
