package com.guardianlabs.safetysos.features.auth.data.remote

import com.guardianlabs.safetysos.features.auth.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body

interface AuthApiService {

    // 1. Specifies the HTTP Method and Endpoint Path
    @POST("auth/register") 
    suspend fun register(
        // 2. Specifies that this parameter should be converted to the JSON Body
        @Body request: RegisterRequestDto 
    ): Response<Unit>

}
