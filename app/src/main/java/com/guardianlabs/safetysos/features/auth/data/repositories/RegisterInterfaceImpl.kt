package com.guardianlabs.safetysos.features.auth.data.repositories

import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import com.guardianlabs.safetysos.core.data.local.preferences.TokenManager
import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.remote.dto.RegisterRequestDto
import com.guardianlabs.safetysos.features.auth.domain.models.User
import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RegisterInterfaceImpl @Inject constructor(
    private val api: AuthApiService, 
    private val dao: UserDao, 
    private val tokenManager: TokenManager
) : RegisterInterface {

    override suspend fun register(
        phone: String, 
        password: String, 
        username: String
    ): Result<User> {
        return try {
            val request = RegisterRequestDto(
                username = username, 
                phone = phone, 
                password = password
            )
            
            val response = api.register(request)

            val user = User(
                navigateToOtp = response.navigateToOtp
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
