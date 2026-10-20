package com.resq.features.settings.data.remote

import com.resq.features.settings.data.remote.dto.LogoutRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingsApiService {

    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    )
}
