package com.guardianlabs.safetysos.features.auth.data.remote

import com.guardianlabs.safetysos.features.auth.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body

interface AuthApiService {

    @POST("auth/register-check") 
    suspend fun register(
        @Body request: RegisterRequestDto 
    ): Response<RegisterResponseDto>
    
    @POST("auth/register") 
    suspend fun registerOtp(
        @Body request: RegisterOtpRequestDto 
    ): Response<RegisterOtpResponseDto>
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>


}
