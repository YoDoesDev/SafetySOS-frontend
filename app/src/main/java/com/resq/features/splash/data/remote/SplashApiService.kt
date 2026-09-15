package com.resq.features.splash.data.remote

import retrofit2.http.GET

interface SplashApiService {
    @GET("api/status")
    suspend fun checkStatus(): String
}