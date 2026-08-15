package com.guardianlabs.safetysos.features.auth.data.repositories

import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import com.guardianlabs.safetysos.core.data.local.preferences.TokenManager
import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.remote.dto.RegisterOtpRequestDto
import com.guardianlabs.safetysos.features.auth.domain.models.User
import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterOtpInterface
import com.guardianlabs.safetysos.core.data.local.database.entities.UserEntity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RegisterInterfaceImpl @Inject constructor(
    private val api: AuthApiService, 
    private val tokenManager: TokenManager, 
    private val dao: UserDao
) : RegisterOtpInterface {

  override suspend fun register(
        username: String, 
        phone: String, 
        password: String, 
        uid: String
    ): Result<User> {
        return try {
            val request = RegisterOtpRequestDto(
                username = username, 
                phone = phone, 
                password = password, 
                uid = uid
            )
            
            val response = api.registerOtp(request)
            
            tokenManager.saveAccessToken(response.accessToken)
            tokenManager.saveRefreshToken(response.refreshToken)
            
            val userObject = UserEntity(
                uid = response.uid, 
                username = response.username, 
                phone = response.phone, 
                passwordHash = response.passwordHash
            )
            
            dao.setUser(user = userObject)

            val user = User(
                username = response.username, 
                phone = response.phone, 
                uid = response.uid, 
                navigateToHome = response.navigateToHome
            )

            Result.success(user)

        } catch (e: IOException) {
            Result.failure(Exception("No internet connection.", e))

        } catch (e: HttpException) {
            Result.failure(Exception("Server error (${e.code()}).", e))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}