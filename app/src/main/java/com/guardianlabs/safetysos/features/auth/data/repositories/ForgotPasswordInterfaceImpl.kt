package com.guardianlabs.safetysos.features.auth.data.repositories

import com.guardianlabs.safetysos.features.auth.data.network.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.network.ForgotPasswordRequestDto
import com.guardianlabs.safetysos.features.auth.data.network.ForgotPasswordResponseDto
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordInterface
import javax.inject.Inject

class ForgotPasswordInterfaceImpl @Inject constructor(
    private val apiService: AuthApiService
) : ForgotPasswordInterface {

    override suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordResponseDto> {
        return try {
            val response = apiService.checkPhone(ForgotPasswordRequestDto(phone))
            val body = response.body()
            
            if (response.isSuccessful && body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error("Server error. Code: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error.")
        }
    }
}
