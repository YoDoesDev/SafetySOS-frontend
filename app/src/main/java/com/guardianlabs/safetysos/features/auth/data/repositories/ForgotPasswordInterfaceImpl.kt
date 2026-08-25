package com.guardianlabs.safetysos.features.auth.data.repositories

import com.google.gson.Gson
import com.guardianlabs.safetysos.features.auth.data.network.NetworkResult
import com.guardianlabs.safetysos.features.auth.data.remote.AuthApiService
import com.guardianlabs.safetysos.features.auth.data.remote.dto.ForgotPasswordRequestDto
import com.guardianlabs.safetysos.features.auth.data.remote.dto.ForgotPasswordResponseDto
import com.guardianlabs.safetysos.features.auth.domain.repositories.ForgotPasswordInterface
import javax.inject.Inject

class ForgotPasswordInterfaceImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val gson: Gson
) : ForgotPasswordInterface {

    override suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordResponseDto> {
        return try {
            val response = apiService.checkPhone(ForgotPasswordRequestDto(phone))
            val body = response.body()

            if (response.isSuccessful && body != null) {
                NetworkResult.Success(body)
            } else {
                // Parse non-200 error response JSON { "success": false, "reason": "..." }
                val errorReason = try {
                    val errorJson = response.errorBody()?.string()
                    val parsedError = gson.fromJson(errorJson, ForgotPasswordResponseDto::class.java)
                    parsedError?.reason
                } catch (e: Exception) {
                    null
                }

                NetworkResult.Error(
                    errorReason ?: "Server error (${response.code()}). Please try again."
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error.")
        }
    }
}
