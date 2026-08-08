package com.guardianlabs.safetysos.features.auth.data.repositories

import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.remote.dto.RegisterRequestDto
import com.guardianlabs.safetysos.features.auth.data.remote.dto.RegisterResponseDto
import com.guardianlabs.safetysos.features.auth.domain.repositories.RegisterInterface
import retrofit2.HttpException
import java.io.IOException
import com.guardianlabs.safetysos.features.auth.domain.models.HiltCheck
import com.guardianlabs.safetysos.core.data.local.database.dao.UserDao
import com.guardianlabs.safetysos.core.data.local.preferences.TokenManager

class RegisterInterfaceImpl(
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
        
            val req = User
            
        } catch (e: IOException) {

            Result.failure(
                Exception("No internet connection.")
            )

        } catch (e: HttpException) {

            Result.failure(
                Exception("Server error (${e.code()}).")
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}