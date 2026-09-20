package com.resq.features.splash.data.remote

import com.resq.features.splash.data.remote.dtos.CheckStatusRequest
import com.resq.features.splash.data.remote.dtos.CheckStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SplashApiService {

    @POST("auth/check-status")
    suspend fun checkStatus(
        @Body request: CheckStatusRequest
    ): Response<CheckStatusResponse>
}
