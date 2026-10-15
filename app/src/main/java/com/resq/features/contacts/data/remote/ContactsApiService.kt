package com.resq.features.contacts.data.remote

import com.resq.features.contacts.data.remote.dto.VerifyUsernameRequest
import com.resq.features.contacts.data.remote.dto.VerifyUsernameResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactsApiService {

    @POST("contacts/verify-username")
    suspend fun verifyUsername(
        @Header("Authorization") token: String?, 
        @Body request: VerifyUsernameRequest,
    ): Response<VerifyUsernameResponse>
}
