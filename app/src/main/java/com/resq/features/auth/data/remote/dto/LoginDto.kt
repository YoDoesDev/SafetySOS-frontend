package com.resq.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("accessToken") val accessToken: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("username") val username: String?
)
