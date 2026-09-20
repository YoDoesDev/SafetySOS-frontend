package com.resq.features.auth.data.repositories


import com.resq.features.auth.data.network.NetworkResult
import com.resq.features.auth.data.remote.AuthApiService
import com.resq.features.auth.data.remote.dto.ForgotPasswordRequestDto
import com.resq.features.auth.domain.models.ForgotPasswordCheck
import com.resq.features.auth.domain.repositories.ForgotPasswordInterface
import java.io.IOException
import javax.inject.Inject

class ForgotPasswordInterfaceImpl @Inject constructor(
    private val api: AuthApiService, 
) : ForgotPasswordInterface {

    override suspend fun checkPhone(phone: String): NetworkResult<ForgotPasswordCheck> {
        return try {
            val request = ForgotPasswordRequestDto(phone = phone)
            val httpResponse = api.checkPhone(request)

            if (httpResponse.isSuccessful && httpResponse.body() != null) {
                val responseDto = httpResponse.body()!!

                val checkDomain = ForgotPasswordCheck(
                    navigateToOtp = responseDto.success
                )

                NetworkResult.Success(checkDomain)
            } else {
                // 1. Read the raw error JSON payload
                val rawError = httpResponse.errorBody()?.string().orEmpty().trim()

                // 2. Extract the "reason" key from the non-200 error response
                val cleanMessage = try {
                    val json = org.json.JSONObject(rawError)
                    // Check "reason" first, then fallback keys if missing
                    json.optString("reason", json.optString("message", "Validation failed"))
                } catch (e: Exception) {
                    rawError.takeIf { it.isNotBlank() } ?: "Validation failed (${httpResponse.code()})"
                }

                NetworkResult.Error(message = cleanMessage, code = httpResponse.code())
            }

        } catch (e: IOException) {
            NetworkResult.Error("No internet connection. Please try again.")

        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "An unexpected error occurred.")
        }
    }
}
