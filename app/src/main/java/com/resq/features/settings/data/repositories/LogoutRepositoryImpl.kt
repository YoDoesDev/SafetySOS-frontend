package com.resq.features.settings.data.repositories

import com.resq.features.settings.data.remote.SettingsApiService
import com.resq.features.settings.data.remote.dto.LogoutRequest
import com.resq.features.settings.domain.repositories.LogoutRepository
import com.resq.core.data.local.preferences.TokenManager
import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.remote.socket.SocketManager
import javax.inject.Inject

class LogoutRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val api: SettingsApiService,
    private val socketManager: SocketManager
) : LogoutRepository {

    override suspend fun logout(): Result<Unit> {
        var networkError: Exception? = null

        try {
            // 1. Attempt server token revocation
            val refreshToken = tokenManager.getRefreshToken()
            val accessToken = tokenManager.getAccessToken()

            if (!refreshToken.isNullOrBlank() && !accessToken.isNullOrBlank()) {
                try {
                    api.logout(LogoutRequest(accessToken, refreshToken))
                } catch (e: Exception) {
                    // Capture network/server failure so we know it failed,
                    // but allow execution to flow down to local cleanup.
                    networkError = e
                }
            }
        } finally {
            // 2. GUARANTEED Local Cleanup: 
            // This runs whether the network call succeeded OR threw an exception.
            userDao.clearUser()
            socketManager.disconnect()
            tokenManager.clearTokens()
        }

        // Return success if everything went smoothly, 
        // or failure if the server ping failed (while still keeping local data wiped).
        return if (networkError == null) {
            Result.success(Unit)
        } else {
            Result.failure(networkError)
        }
    }
}
