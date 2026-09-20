package com.resq.features.auth.data.remote

import com.resq.features.auth.data.remote.dto.*
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

    @POST("auth/check-phone")
    suspend fun checkPhone(
        @Body request: ForgotPasswordRequestDto
    ): Response<ForgotPasswordResponseDto>
    
    @POST("auth/forgot-password-login")
    suspend fun forgotPasswordLogin(
        @Body request: ForgotPasswordOtpRequestDto
    ): Response<ForgotPasswordOtpResponseDto>
    
}
