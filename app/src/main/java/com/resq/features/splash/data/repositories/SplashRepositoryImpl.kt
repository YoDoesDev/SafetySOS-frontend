package com.resq.features.splash.data.repositories

import com.resq.core.data.local.preferences.TokenManager
import com.resq.features.splash.data.remote.SplashApiService
import com.resq.features.splash.data.remote.dtos.CheckStatusRequest
import com.resq.features.splash.domain.repositories.SplashRepository
import java.io.IOException
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
    private val apiService: SplashApiService, 
    private val tokenManager: TokenManager
) : SplashRepository {

    override suspend fun checkStatus(): Boolean {
        return try {
            val aToken: String? = tokenManager.getAccessToken()
            val rToken: String? = tokenManager.getRefreshToken()
            
            val request = CheckStatusRequest(
               aToken = aToken, 
               rToken = rToken
            )
            
            val response = apiService.checkStatus(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    body.aToken?.let { newToken ->
                        tokenManager.saveAccessToken(newToken)
                    }
                    body.rToken?.let { newToken ->
                        tokenManager.saveRefreshToken(newToken)
                    }
                    tokenManager.resetSessionExpiry()
                    true
                } else {
                    // Server returned 200 OK, but payload success flag was false
                    tokenManager.isSessionValidLocally()
                }
            } else {
                // Server returned HTTP 500 (Server Error / DB issue)
                // Fail-open to allow offline core SOS features!
                tokenManager.isSessionValidLocally()
            }
        } catch (e: IOException) {
            // No Internet / Airplane Mode: Fail-open to local session
            tokenManager.isSessionValidLocally()
        } catch (e: Exception) { 
            // Unexpected execution error: Fail-open if refresh token exists locally
            tokenManager.hasRefreshToken() || tokenManager.isSessionValidLocally()
        }
    }
}
