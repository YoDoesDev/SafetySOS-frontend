package com.resq.features.auth.data.repositories

import com.resq.core.data.local.database.dao.UserDao
import com.resq.core.data.local.database.entities.UserEntity
import com.resq.core.data.local.preferences.TokenManager
import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.auth.data.remote.dto.RegisterOtpRequestDto
import com.resq.features.auth.domain.models.User
import com.resq.features.auth.domain.repositories.RegisterOtpInterface
import java.io.IOException
import javax.inject.Inject

class RegisterOtpInterfaceImpl @Inject constructor(
    private val api: AuthApiService, 
    private val tokenManager: TokenManager, 
    private val dao: UserDao
) : RegisterOtpInterface {

    override suspend fun register(
        username: String, 
        phone: String, 
        password: String, 
        uid: String
    ): NetworkResult<User> {
        return try {
            val request = RegisterOtpRequestDto(
                username = username, 
                phone = phone, 
                password = password, 
                uid = uid
            )
            
            // 1. Make network call (returns Response<RegisterOtpResponseDto>)
            val httpResponse = api.registerOtp(request)

            if (httpResponse.isSuccessful && httpResponse.body() != null) {
                val responseDto = httpResponse.body()!!

                // Safely save tokens if present
                responseDto.accessToken?.let {
                    tokenManager.saveAccessToken(it) 
                    tokenManager.resetSessionExpiry()
                }
                responseDto.refreshToken?.let {
                    tokenManager.saveRefreshToken(it) 
                    tokenManager.resetSessionExpiry()
                }
                
                
                // Save room cache safely
                val userEntity = UserEntity(
                    username = responseDto.username ?: "", 
                    phone = responseDto.phone ?: ""
                )
                dao.setUser(user = userEntity)

                // Map DTO to Domain Model safely using default values
                val userDomainModel = User(
                    username = responseDto.username ?: "", 
                    phone = responseDto.phone ?: "", 
                    uid = responseDto.uid ?: "", 
                    navigateToHome = responseDto.navigateToHome ?: false
                )

                NetworkResult.Success(userDomainModel)
            } else {
                // HTTP Error (400, 401, 500, etc.)
                val errorMsg = httpResponse.errorBody()?.string() ?: "Registration failed"
                NetworkResult.Error(message = errorMsg, code = httpResponse.code())
            }

        } catch (e: IOException) {
            // Internet disconnects / socket timeouts
            NetworkResult.Error(message = "No internet connection. Please try again.")

        } catch (e: Exception) {
            // Catches JSON parsing errors or unhandled exceptions without crashing
            NetworkResult.Error(message = e.localizedMessage ?: "An unexpected error occurred.")
        }
    }
}
