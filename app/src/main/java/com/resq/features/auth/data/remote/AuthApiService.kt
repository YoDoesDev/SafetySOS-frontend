package com.resq.features.auth.data.remote

import com.resq.features.auth.data.remote.dto.LoginRequestDto
import com.resq.features.auth.data.remote.dto.LoginResponseDto
import com.resq.features.auth.data.remote.dto.RegisterDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterDto): LoginResponseDto
}